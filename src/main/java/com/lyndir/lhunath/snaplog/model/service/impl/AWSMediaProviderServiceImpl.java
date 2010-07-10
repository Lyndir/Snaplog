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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.logging.exception.InternalInconsistencyException;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.system.util.StringUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3MediaData;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.AWSMediaProviderService;
import com.lyndir.lhunath.snaplog.model.service.AWSService;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.util.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;


/**
 * <h2>{@link AWSMediaProviderServiceImpl}<br>
 *
 * <p> <i>Jan 10, 2010</i> </p>
 *
 * @author lhunath
 */
public class AWSMediaProviderServiceImpl implements AWSMediaProviderService {

    private static final Logger logger = Logger.get( AWSMediaProviderServiceImpl.class );

    private final MediaDAO mediaDAO;
    private final AWSService awsService;
    private final UserService userService;
    private final SecurityService securityService;

    /**
     * @param mediaDAO        See {@link ServicesModule}.
     * @param awsService      See {@link ServicesModule}.
     * @param userService     See {@link ServicesModule}.
     * @param securityService See {@link ServicesModule}.
     */
    @Inject
    public AWSMediaProviderServiceImpl(final MediaDAO mediaDAO, final AWSService awsService, final UserService userService,
                                       final SecurityService securityService) {

        this.mediaDAO = mediaDAO;
        this.awsService = awsService;
        this.userService = userService;
        this.securityService = securityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMedia(final S3Album album) {

        checkNotNull( album, "Given album must not be null." );

        // TODO: Remove media that has disappeared.
        int o = 0;
        ImmutableList<S3Object> mediaObjects = awsService.listObjects( getObjectKey( album, Quality.ORIGINAL ) );
        for (final S3Object mediaObject : mediaObjects) {
            if (o++ % 100 == 0)
                logger.dbg( "Loading media %d / %d", ++o, mediaObjects.size() );

            if (!mediaObject.getKey().endsWith( ".jpg" ))
                // Ignore files that don't have a valid media name.
                continue;

            String mediaName = Iterables.getLast( Splitter.on( '/' ).split( mediaObject.getKey() ) );

            if (mediaName.startsWith( "." ))
                // Ignore hidden files.
                continue;

            // Create/update mediaData for the object.
            setMediaData( album, mediaName, Quality.ORIGINAL, mediaObject );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMediaData(final S3Album album) {

        checkNotNull( album, "Given album must not be null." );

        int o = 0;
        List<S3Media> albumMedia = mediaDAO.listMedia( album );
        for (final S3Media media : albumMedia) {
            if (o++ % 100 == 0)
                logger.dbg( "Loading media data %d / %d", ++o, albumMedia.size() );

            // Load existing and create missing media objects at all qualities.
            for (final Quality quality : Quality.values())
                loadObjectDetails( media, quality );
        }
    }

    /**
     * Update meta data by assign metadata to a given quality of it.
     *
     * @param album       The album in which to look for the media.
     * @param mediaName   The name of the media to look for.  If no media exists by this name, <b>it will be created</b>.
     * @param quality     The quality of the metadata to update the media data with.  May be <code>null</code> if no updating should occur
     *                    but we're just interested in obtaining the media's media data.
     * @param mediaObject The object to save in the media's data under the given quality.
     *
     * @return A media data object for the given media with the metadata at the given quality updated if desired.
     */
    private S3MediaData setMediaData(final S3Album album, final String mediaName, final Quality quality, final S3Object mediaObject) {

        S3MediaData mediaData = mediaDAO.findMediaData( album, mediaName );
        boolean needsUpdate = false;

        if (mediaData == null) {
            mediaData = new S3MediaData( new S3Media( album, mediaName ) );
            needsUpdate = true;
        }

        if (quality != null && !ObjectUtils.equal( mediaData.get( quality ), mediaObject )) {
            mediaData.put( quality, mediaObject );
            needsUpdate = true;
        }

        if (needsUpdate)
            mediaDAO.update( mediaData );

        return mediaData;
    }

    /**
     * Update meta data by assign metadata to a given quality of it.
     *
     * @param media       The media whose media data is required.
     * @param quality     The quality of the metadata to update the media data with.  May be <code>null</code> if no updating should occur
     *                    but we're just interested in obtaining the media's media data.
     * @param mediaObject The object to save in the media's data under the given quality.
     *
     * @return A media data object for the given media with the metadata at the given quality updated if desired.
     */
    private S3MediaData setMediaData(final S3Media media, final Quality quality, final S3Object mediaObject) {

        return setMediaData( media.getAlbum(), media.getName(), quality, mediaObject );
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
     * Obtain media data for the given media and indicate metadata for the given quality is required.
     *
     * @param media   The media whose media data is required.
     * @param quality The quality whose metadata is needed.
     *
     * @return A media data object for the given media with metadata for the given quality present <b>if the object at the given quality
     *         exists in storage</b>.
     */
    private S3MediaData getMediaData(final S3Media media, final Quality quality) {

        logger.dbg( "Finding S3 object details of: %s, at: %s", media, quality );
        S3MediaData mediaData = getMediaData( media );

        S3Object mediaObject = mediaData.get( quality );
        if (mediaObject == null) {
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
    public URL findResourceURL(final SecurityToken token, final S3Media media, final Quality quality)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );
        securityService.assertAccess( Permission.VIEW, token, media );

        S3Object mediaObject = findObjectDetails( media, quality );
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
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(final SecurityToken token, final S3Media media)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );
        securityService.assertAccess( Permission.VIEW, token, media );

        return getObject( media ).getLastModifiedDate().getTime();
    }

    /**
     * Retrieve the object key for all resources of the given {@link Album} at the given quality.
     *
     * @param album   The album whose resources are contained under the key.
     * @param quality The quality of the resources that are contained under the key.
     *
     * @return An S3 object key within the bucket.
     */
    protected static String getObjectKey(final S3Album album, final Quality quality) {

        return StringUtils.concat( "/", "users", album.getOwnerProfile().getUser().getUserName(), album.getName(), quality.getName() );
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

        return StringUtils.concat( "/", getObjectKey( media.getAlbum(), quality ), media.getName() );
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
     * Look up all metadata for media at a certain quality.
     *
     * @param media   The {@link Media} whose data is will be referenced by the returned object.
     * @param quality The {@link Quality} of the {@link Media}'s data.
     *
     * @return An {@link S3Object} with metadata or <code>null</code> if no object exists in S3 for the given media at the given quality.
     *
     * @see S3Service#getObject(S3Bucket, String)
     */
    protected S3Object findObjectDetails(final S3Media media, final Quality quality) {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );

        return getMediaData( media, quality ).get( quality );
    }

    /**
     * Retrieve all metadata for media at a certain quality.  If the media does not yet exist in storage at the given quality, it will be
     * generated from original quality first. <b>This operation can take quite some time.</b>
     *
     * @param media   The {@link Media} whose data is will be referenced by the returned object.
     * @param quality The {@link Quality} of the {@link Media}'s data.
     *
     * @return An {@link S3Object} with all the media storage object's metadata.
     */
    protected S3Object loadObjectDetails(final S3Media media, final Quality quality) {

        S3Object s3ResourceObject = findObjectDetails( media, quality );
        if (s3ResourceObject != null)
            return s3ResourceObject;

        // Read the original.
        if (quality == Quality.ORIGINAL)
            throw logger.err( "Media's original resource does not exist." ).toError();

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
                throw logger.err( e, "Image data could not be read: %s", s3OriginalObject ) //
                        .toError();
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
            throw logger.err( e, "Image data could not be read: %s", s3OriginalObject ) //
                    .toError();
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
     * @return An {@link S3Object} with basic metadata.
     *
     * @see S3Service#listObjects(S3Bucket)
     */
    protected S3Object getObject(final S3Media media) {

        checkNotNull( media, "Given media must not be null." );

        S3MediaData mediaData = getMediaData( media );
        return checkNotNull( mediaData.get( Quality.METADATA ), "S3 object for %s must not be null.", media );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Album newAlbum(final User ownerUser, final String albumName, final String albumDescription) {

        try {
            S3Album album = new S3Album( userService.getProfile( SecurityToken.INTERNAL_USE_ONLY, ownerUser ), albumName );
            album.setDescription( albumDescription );

            return album;
        }

        catch (PermissionDeniedException e) {
            throw new InternalInconsistencyException( "Permission denied for INTERNAL_USE_ONLY?", e );
        }
    }
}
