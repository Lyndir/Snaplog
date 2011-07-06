/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.model.service.impl;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.snaplog.security.SnaplogST;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.opal.security.service.SecurityService;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.logging.exception.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.system.util.Throw;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.media.aws.*;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.data.service.SourceDAO;
import com.lyndir.lhunath.snaplog.model.ServiceModule;
import com.lyndir.lhunath.snaplog.model.service.AWSService;
import com.lyndir.lhunath.snaplog.model.service.AWSSourceService;
import com.lyndir.lhunath.snaplog.util.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;


/**
 * <h2>{@link AWSSourceServiceImpl}<br>
 *
 * <p> <i>Jan 10, 2010</i> </p>
 *
 * @author lhunath
 */
public class AWSSourceServiceImpl extends AbstractSourceService<S3Source, S3Media> implements AWSSourceService {

    private static final Logger logger = Logger.get( AWSSourceServiceImpl.class );

    private final AWSService awsService;

    /**
     * @param mediaDAO        See {@link ServiceModule}.
     * @param sourceDAO       See {@link ServiceModule}.
     * @param awsService      See {@link ServiceModule}.
     * @param securityService See {@link ServiceModule}.
     */
    @Inject
    public AWSSourceServiceImpl(final MediaDAO mediaDAO, final SourceDAO sourceDAO, final AWSService awsService,
                                final SecurityService securityService) {

        super( securityService, sourceDAO, mediaDAO );

        this.awsService = awsService;
    }

    @Override
    public void loadMedia(final SnaplogST token, final S3Source source)
            throws PermissionDeniedException {

        checkNotNull( source, "Given source must not be null." );
        securityService.assertAccess( Permission.ADMINISTER, token, source );

        // Fetch objects at all qualities from S3
        Map<String, Map<Quality, S3Object>> mediaObjects = Maps.newHashMap();
        for (final Quality quality : Quality.values()) {

            for (final S3Object s3Object : awsService.listObjects( getObjectKey( source, quality ) )) {

                if (!s3Object.getKey().endsWith( ".jpg" ))
                    // Ignore files that don't have a valid media name.
                    continue;

                String mediaName = Iterables.getLast( Splitter.on( '/' ).split( s3Object.getKey() ) );
                if (mediaName.startsWith( "." ))
                    // Ignore hidden files.
                    continue;

                Map<Quality, S3Object> qualityObjects = mediaObjects.get( mediaName );
                if (qualityObjects == null)
                    mediaObjects.put( mediaName, qualityObjects = Maps.newHashMap() );
                qualityObjects.put( quality, s3Object );
            }
        }

        // Find all existing media that is not contained in the set of media fetched from S3
        // These are media that have been removed from S3 since the last sync; purge them.
        logger.dbg( "Looking for media to purge..." );
        ImmutableMap.Builder<String, Media> existingMediaBuilder = ImmutableMap.builder();
        for (final Media media : mediaDAO.listMedia( source, true ))
            existingMediaBuilder.put( media.getName(), media );
        ImmutableMap<String, Media> existingMedia = existingMediaBuilder.build();
        Set<Media> purgeMedia = Sets.newHashSet( existingMedia.values() );
        for (final String mediaName : mediaObjects.keySet()) {
            Media media = existingMedia.get( mediaName );
            if (media != null)
                // This media was found in S3's list of current media data; don't purge it.
                purgeMedia.remove( media );
        }
        logger.dbg( "Purging %d / %d media from db", purgeMedia.size(), existingMedia.size() );
        mediaDAO.delete( purgeMedia );

        int o = 0;
        for (final Map.Entry<String, Map<Quality, S3Object>> mediaObjectsEntry : mediaObjects.entrySet()) {
            if (o++ % 100 == 0)
                logger.dbg( "Loading media %d / %d", ++o, mediaObjects.size() );

            String mediaName = mediaObjectsEntry.getKey();
            Map<Quality, S3Object> qualityObjects = mediaObjectsEntry.getValue();

            S3Media media = mediaDAO.findMedia( source, mediaName );
            if (media == null)
                media = new S3Media( source, mediaName );

            // Create/update mediaData for the object.
            for (final Map.Entry<Quality, S3Object> qualityObjectsEntry : qualityObjects.entrySet()) {
                Quality quality = qualityObjectsEntry.getKey();
                S3Object mediaObject = qualityObjectsEntry.getValue();

                setMediaData( media, quality, mediaObject );
            }
        }
    }

    @Override
    public void loadMediaData(final SnaplogST token, final S3Source source)
            throws PermissionDeniedException {

        checkNotNull( source, "Given source must not be null." );
        securityService.assertAccess( Permission.ADMINISTER, token, source );

        List<S3MediaData> mediaDatas = mediaDAO.listMediaData( source, false );

        int o = 0;
        for (final S3MediaData mediaData : mediaDatas) {
            if (o++ % 100 == 0)
                logger.dbg( "Loading media data %d / %d", ++o, mediaDatas.size() );

            // Load existing and create missing media objects at all qualities.
            for (final Quality quality : Quality.values())
                if (mediaData.get( quality ) == null && mediaData.get( Quality.ORIGINAL ) != null)
                    loadObjectDetails( mediaData.getMedia(), quality, true );
        }
    }

    /**
     * Update meta data by assign metadata to a given quality of it.  If the media's data does not exist; it will be created.
     *
     * @param media       The media whose media data is required.
     * @param quality     The quality of the metadata to update the media data with.  May be <code>null</code> if no updating should occur
     *                    but we're just interested in obtaining the media's media data.
     * @param mediaObject The object to save in the media's data under the given quality.
     *
     * @return A media data object for the given media with the metadata at the given quality updated if desired.
     */
    private S3MediaData setMediaData(final S3Media media, final Quality quality, final S3Object mediaObject) {

        S3MediaData mediaData = mediaDAO.findMediaData( media );
        boolean needsUpdate = false;

        if (mediaData == null) {
            mediaData = new S3MediaData( media );
            needsUpdate = true;
        }

        if (quality != null && !ObjectUtils.isEqual( mediaData.get( quality ), mediaObject )) {
            mediaData.put( quality, mediaObject );
            needsUpdate = true;
        }

        if (needsUpdate)
            mediaDAO.update( mediaData );

        return mediaData;
    }

    /**
     * Obtain media data for the given media.
     *
     * @param media The media whose media data is required.
     *
     * @return A media data object for the given media.
     */
    private S3MediaData getMediaData(final S3Media media) {

        return setMediaData( media, null, null );
    }

    /**
     * Obtain media data for the given media.
     *
     * @param media    The media whose media data is needed.
     * @param quality  The quality whose storage object is needed.
     * @param metadata <code>true</code> if the storage object's full metadata is necessary.
     *
     * @return A media data object for the given media with metadata for the given quality present <b>if the object at the given quality
     *         exists in storage</b>.
     */
    private S3MediaData getMediaData(final S3Media media, final Quality quality, final boolean metadata) {

        logger.dbg( "Finding S3 object details of: %s, at: %s", media, quality );
        S3MediaData mediaData = getMediaData( media );

        S3Object mediaObject = mediaData.get( quality );
        if (mediaObject == null || metadata && !mediaObject.isMetadataComplete()) {
            mediaObject = awsService.fetchObjectDetails( getObjectKey( media, quality ) );

            if (mediaObject != null) {
                mediaData.put( quality, mediaObject );
                mediaDAO.update( mediaData );
            }
        }

        return mediaData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL findResourceURL(final SnaplogST token, final S3Media media, final Quality quality)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );
        securityService.assertAccess( Permission.VIEW, token, media );

        S3Object mediaObject = findObjectDetails( media, quality, false );
        if (mediaObject == null)
            return null;

        String s3ResourcePath = mediaObject.getKey();
        logger.dbg( "Resolved S3 object for: %s, at: %s, to path: %s", media, quality, s3ResourcePath );

        try {
            return new URL( String.format( "http://snaplog.net.s3.amazonaws.com/%s", s3ResourcePath ) );
        }

        catch (MalformedURLException e) {
            throw new InternalInconsistencyException( "Couldn't construct a valid URL for S3 resource.", e );
        }
    }

    /**
     * Retrieve the object key for all resources of the given {@link Source} at the given quality.
     *
     * @param source   The source that provides the resources.
     * @param quality The quality of the resources.
     *
     * @return An S3 object key within the bucket.
     */
    protected static String getObjectKey(final S3Source source, final Quality quality) {

        return String.format( "%s/%s", source.getPrefix(), quality.getName() );
    }

    /**
     * Retrieve the object key for the resource of the given {@link Media} at the given quality.
     *
     * @param media   The media whose resource is referenced the key.
     * @param quality The quality of the referenced resource.
     *
     * @return An S3 object key within the bucket.
     */
    protected static String getObjectKey(final S3Media media, final Quality quality) {

        return String.format( "%s/%s", getObjectKey( media.getSource(), quality ), media.getName() );
    }

    /**
     * Get an {@link S3Object} with all metadata and a data stream available.
     *
     * <p> <b>Note:</b> The data stream to this object remains open so you can use it. <b>Don't forget to close it</b> when you're done!
     * </p>
     *
     * @param media   The {@link Media} whose data is will be referenced by the returned object.
     * @param quality The {@link Quality} of the {@link Media}'s data.
     *
     * @return An {@link S3Object} with metadata and a data stream.
     *
     * @see S3Service#getObject(S3Bucket, String)
     */
    protected S3Object readObject(final S3Media media, final Quality quality) {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );

        S3Object mediaObject = awsService.readObject( getObjectKey( media, quality ) );
        setMediaData( media, quality, mediaObject );

        return checkNotNull( mediaObject, "S3 object must not be null." );
    }

    /**
     * Look up the storage object for media at a certain quality.
     *
     * @param media    The {@link Media} whose data is will be referenced by the returned object.
     * @param quality  The {@link Quality} of the {@link Media}'s data.
     * @param metadata <code>true</code> if the storage object's full metadata is necessary.
     *
     * @return An {@link S3Object} with metadata or <code>null</code> if no object exists in S3 for the given media at the given quality.
     *
     * @see S3Service#getObject(S3Bucket, String)
     */
    protected S3Object findObjectDetails(final S3Media media, final Quality quality, final boolean metadata) {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );

        return getMediaData( media, quality, metadata ).get( quality );
    }

    /**
     * Retrieve the storage object for media at a certain quality.  If the media does not yet exist in storage at the given quality, it will
     * be generated from original quality first. <b>This operation can take quite some time.</b>
     *
     * @param media    The {@link Media} whose data is will be referenced by the returned object.
     * @param quality  The {@link Quality} of the {@link Media}'s data.
     * @param metadata <code>true</code> if the storage object's full metadata is necessary.
     *
     * @return An {@link S3Object} with all the media storage object's metadata.
     */
    protected S3Object loadObjectDetails(final S3Media media, final Quality quality, final boolean metadata) {

        S3Object s3ResourceObject = findObjectDetails( media, quality, metadata );
        if (s3ResourceObject != null)
            return s3ResourceObject;

        // Read the original.
        checkArgument( quality != Quality.ORIGINAL, "Can't generate original media resource");

        logger.inf( "S3 does not yet have an object for: %s, at quality: %s", media, quality );
        S3Object s3OriginalObject = awsService.readObject( getObjectKey( media, Quality.ORIGINAL ) );
        ByteArrayOutputStream imageDataStream = new ByteArrayOutputStream();

        // Rescale to the appropriate quality.
        try {
            InputStream s3InputStream = s3OriginalObject.getDataInputStream();
            try {
                BufferedImage qualityImage = ImageIO.read( s3InputStream );
                logger.dbg( "Read original image with dimensions %dx%d", qualityImage.getWidth(), qualityImage.getHeight() );
                ImageUtils.write( ImageUtils.rescale( qualityImage, quality.getMaxWidth(), quality.getMaxHeight() ), //
                                  imageDataStream, "image/jpeg", quality.getCompression(), true );
            }
            catch (IOException e) {
                throw Throw.propagate( e, "Image data could not be read: %s", s3OriginalObject );
            }
            finally {
                try {
                    s3InputStream.close();
                }
                catch (IOException e) {
                    logger.err( e, "S3 original resource read stream cleanup failed for object: %s", s3OriginalObject );
                }
            }
        }
        catch (S3ServiceException e) {
            throw Throw.propagate( e, "Image data could not be read: %s", s3OriginalObject );
        }
        logger.dbg( "Wrote rescaled image of quality: %s, size: %d", quality, imageDataStream.size() );

        // Upload to S3.
        // TODO: Could probably improve this by using Piped*Stream instead and multi-threading instead.
        S3Object s3UploadObject = new S3Object( getObjectKey( media, quality ) );
        s3UploadObject.setContentType( "image/jpeg" );
        s3UploadObject.setContentLength( imageDataStream.size() );
        s3UploadObject.setAcl( AccessControlList.REST_CANNED_PUBLIC_READ );
        s3UploadObject.setDataInputStream( new ByteArrayInputStream( imageDataStream.toByteArray() ) );

        setMediaData( media, quality, s3ResourceObject = awsService.upload( s3UploadObject ) );

        return s3ResourceObject;
    }

    /**
     * Get an {@link S3Object} with full metadata available.
     *
     * @param media The {@link Media} whose data is will be referenced by the returned object.
     *
     * @return An {@link S3Object} with full metadata.
     *
     * @see S3Service#listObjects(S3Bucket)
     */
    protected S3Object getObject(final S3Media media) {

        checkNotNull( media, "Given media must not be null." );

        return getMediaData( media, Quality.ORIGINAL, true ).get( Quality.ORIGINAL );
    }
}
