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
package com.lyndir.lhunath.snaplog.model.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.logging.exception.InternalInconsistencyException;
import com.lyndir.lhunath.lib.system.util.SafeObjects;
import com.lyndir.lhunath.lib.system.util.StringUtils;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.media.aws.S3AlbumData;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.media.aws.S3MediaData;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AWSMediaProviderService;
import com.lyndir.lhunath.snaplog.model.AWSService;
import com.lyndir.lhunath.snaplog.model.SecurityService;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.util.ImageUtils;


/**
 * <h2>{@link AWSMediaProviderServiceImpl}<br>
 * 
 * <p>
 * <i>Jan 10, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class AWSMediaProviderServiceImpl implements AWSMediaProviderService {

    private static final Logger logger = Logger.get( AWSMediaProviderServiceImpl.class );

    private static final Pattern BASENAME = Pattern.compile( ".*/" );

    private final ObjectContainer db;
    private final AWSService awsService;
    private final UserService userService;
    private final SecurityService securityService;


    /**
     * @param db
     *            See {@link ServicesModule}.
     * @param awsService
     *            See {@link ServicesModule}.
     * @param userService
     *            See {@link ServicesModule}.
     * @param securityService
     *            See {@link ServicesModule}.
     */
    @Inject
    public AWSMediaProviderServiceImpl(ObjectContainer db, AWSService awsService, UserService userService,
                                       SecurityService securityService) {

        this.db = db;
        this.awsService = awsService;
        this.userService = userService;
        this.securityService = securityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<S3Media> iterateFiles(final SecurityToken token, S3Album album) {

        checkNotNull( album, "Given album must not be null." );

        List<S3Media> files = new LinkedList<S3Media>();
        for (S3Object albumObject : awsService.listObjects( getObjectKey( album, Quality.ORIGINAL ) )) {

            String mediaName = BASENAME.matcher( albumObject.getKey() ).replaceFirst( "" );
            S3MediaData mediaData = getMediaData( new S3Media( album, mediaName ) );
            mediaData.put( Quality.METADATA, albumObject );
            db.store( mediaData );

            S3Media media = mediaData.getMedia();
            if (securityService.hasAccess( Permission.VIEW, token, media ))
                files.add( media );
        }

        return files.iterator();
    }

    private S3MediaData getMediaData(final S3Media media) {

        ObjectSet<S3MediaData> s3mediaDataQuery = db.query( new Predicate<S3MediaData>() {

            @Override
            public boolean match(S3MediaData candidate) {

                return SafeObjects.equal( candidate.getMedia(), media );
            }
        } );
        if (s3mediaDataQuery.hasNext())
            return s3mediaDataQuery.next();

        S3MediaData s3MediaData = new S3MediaData( media );
        db.store( s3MediaData );

        return s3MediaData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResourceURL(final SecurityToken token, S3Media media, Quality quality)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );
        securityService.assertAccess( Permission.VIEW, token, media );

        S3Object s3ResourceObject = findObjectDetails( media, quality );
        if (s3ResourceObject == null) {
            if (quality == Quality.ORIGINAL)
                throw logger.bug( "Can't create a file's original resource." ) //
                            .toError( UnsupportedOperationException.class );

            // Read the original.
            logger.inf( "S3 does not yet have an object for: %s, at quality: %s", media, quality );
            S3Object s3OriginalObject = awsService.readObject( getObjectKey( media, Quality.ORIGINAL ) );
            ByteArrayOutputStream imageDataStream = new ByteArrayOutputStream();

            // Rescale to the appropriate quality.
            try {
                InputStream s3InputStream = s3OriginalObject.getDataInputStream();
                try {
                    BufferedImage qualityImage = ImageIO.read( s3InputStream );
                    logger.dbg( "Read original image with dimensions %dx%d", qualityImage.getWidth(),
                                qualityImage.getHeight() );
                    ImageUtils.write(
                                      ImageUtils.rescale( qualityImage, quality.getMaxWidth(), quality.getMaxHeight() ), //
                                      imageDataStream, "image/jpeg", quality.getCompression(), true );
                } catch (IOException e) {
                    throw logger.err( e, "Image data could not be read: %s", s3OriginalObject ) //
                                .toError();
                } finally {
                    try {
                        s3InputStream.close();
                    } catch (IOException e) {
                        logger.err( e, "S3 original resource read stream cleanup failed for object: %s",
                                    s3OriginalObject );
                    }
                }
            } catch (S3ServiceException e) {
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

            S3MediaData mediaData = getMediaData( media );
            mediaData.put( quality, s3ResourceObject = awsService.upload( s3UploadObject ) );
            db.store( mediaData );
        }

        try {
            return new URL( String.format( "http://snaplog.net.s3.amazonaws.com/%s", s3ResourceObject.getKey() ) );
        }

        catch (MalformedURLException e) {
            throw new InternalInconsistencyException( "Couldn't construct a valid URL for S3 resource.", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(final SecurityToken token, S3Media media)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );
        securityService.assertAccess( Permission.VIEW, token, media );

        return getObject( media ).getLastModifiedDate().getTime();
    }

    /**
     * Retrieve the object key for all resources of the given {@link Album} at the given quality.
     * 
     * @param album
     *            The album whose resources are contained under the key.
     * @param quality
     *            The quality of the resources that are contained under the key.
     * 
     * @return An S3 object key within the bucket.
     */
    protected String getObjectKey(S3Album album, Quality quality) {

        return StringUtils.concat( "/", "users", album.getOwnerProfile().getUser().getUserName(), album.getName(),
                                   quality.getName() );
    }

    /**
     * Retrieve the object key for the resource of the given {@link Media} at the given quality.
     * 
     * @param media
     *            The media whose resource is referenced the key.
     * @param quality
     *            The quality of the referenced resource.
     * 
     * @return An S3 object key within the bucket.
     */
    protected String getObjectKey(S3Media media, Quality quality) {

        return StringUtils.concat( "/", getObjectKey( media.getAlbum(), quality ), media.getName() );
    }

    /**
     * Get an {@link S3Object} with all metadata and a data stream available.
     * 
     * <p>
     * <b>Note:</b> The data stream to this object remains open so you can use it. <b>Don't forget to close it</b> when
     * you're done!
     * </p>
     * 
     * @param media
     *            The {@link Media} whose data is will be referenced by the returned object.
     * @param quality
     *            The {@link Quality} of the {@link Media}'s data.
     * 
     * @return An {@link S3Object} with metadata and a data stream.
     * 
     * @see S3Service#getObject(S3Bucket, String)
     */
    protected S3Object readObject(S3Media media, Quality quality) {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );

        S3Object s3Object = awsService.readObject( getObjectKey( media, quality ) );
        S3MediaData mediaData = getMediaData( media );
        mediaData.put( quality, s3Object );
        db.store( mediaData );

        return checkNotNull( s3Object, "S3 object must not be null." );
    }

    /**
     * Look up all metadata for media at a certain quality.
     * 
     * @param media
     *            The {@link Media} whose data is will be referenced by the returned object.
     * @param quality
     *            The {@link Quality} of the {@link Media}'s data.
     * 
     * @return An {@link S3Object} with metadata or <code>null</code> if no object exists for the given media at the
     *         given quality.
     * 
     * @see S3Service#getObject(S3Bucket, String)
     */
    protected S3Object findObjectDetails(S3Media media, Quality quality) {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );

        S3MediaData mediaData = getMediaData( media );
        S3Object s3Object = mediaData.get( quality );
        if (s3Object == null) {
            s3Object = awsService.findObjectDetails( getObjectKey( media, quality ) );
            if (s3Object != null) {
                mediaData.put( quality, s3Object );
                db.store( mediaData );
            }
        }

        return s3Object;
    }

    /**
     * Get an {@link S3Object} with very basic metadata available.
     * 
     * @param media
     *            The {@link Media} whose data is will be referenced by the returned object.
     * 
     * @return An {@link S3Object} with basic metadata.
     * 
     * @see S3Service#listObjects(S3Bucket)
     */
    protected S3Object getObject(S3Media media) {

        checkNotNull( media, "Given media must not be null." );

        return checkNotNull( getMediaData( media ).get( Quality.METADATA ), "S3 object for %s must not be null.", media );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3AlbumData newAlbumData(S3Album album) {

        checkNotNull( album, "Given album must not be null." );

        return new S3AlbumData( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Album newAlbum(User ownerUser, String albumName, String albumDescription) {

        try {
            S3Album album = new S3Album( userService.getProfile( SecurityToken.INTERNAL_USE_ONLY, ownerUser ),
                    albumName );
            album.setDescription( albumDescription );

            return album;
        }

        catch (PermissionDeniedException e) {
            throw new InternalInconsistencyException( "Permission denied for INTERNAL_USE_ONLY?", e );
        }
    }
}
