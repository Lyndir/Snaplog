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
import static com.google.common.base.Preconditions.checkState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.StringUtils;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.data.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.aws.S3Media;
import com.lyndir.lhunath.snaplog.model.AWSMediaProviderService;
import com.lyndir.lhunath.snaplog.model.AWSService;
import com.lyndir.lhunath.snaplog.util.ImageUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;


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

    private static final Map<S3Media, Map<Quality, S3Object>> s3MediaQualityObjects = new HashMap<S3Media, Map<Quality, S3Object>>();
    private static final Pattern BASENAME = Pattern.compile( ".*/" );

    private final AWSService awsService;


    /**
     * @param awsService See {@link AWSService} 
     */
    @Inject
    public AWSMediaProviderServiceImpl(AWSService awsService) {

        this.awsService = awsService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<? extends Media> getFiles(S3Album album) {

        Builder<S3Media> filesBuilder = new Builder<S3Media>();
        for (S3Object albumObject : awsService.listObjects( getObjectKey( album, Quality.ORIGINAL ) )) {

            Maps.newHashMapWithExpectedSize( Quality.values().length );

            String mediaName = BASENAME.matcher( albumObject.getKey() ).replaceFirst( "" );
            S3Media media = new S3Media( album, mediaName );
            filesBuilder.add( media );

            s3MediaQualityObjects.put( media, new EnumMap<Quality, S3Object>(Quality.class) );
        }

        logger.dbg( "%d entries in s3MediaQualityObjects", s3MediaQualityObjects.size() );
        return filesBuilder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getResourceURI(S3Media media, Quality quality) {

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
                        logger.err( e, "S3 original resource read stream cleanup failed for object: %s", s3OriginalObject );
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
            s3ResourceObject = awsService.upload( s3UploadObject );
        }

        return URI.create( String.format( "http://snaplog.net.s3.amazonaws.com/%s", s3ResourceObject.getKey() ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(S3Media media) {

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
    protected String getObjectKey(Album album, Quality quality) {

        return StringUtils.concat( "/", "users", album.getUser().getUserName(), album.getName(), quality.getName() );
    }

    /**
     * Retrieve the object key for the resource of the given {@link Media} at the given quality.
     *
     * @param media   The media whose resource is referenced the key.
     * @param quality The quality of the referenced resource.
     *
     * @return An S3 object key within the bucket.
     */
    protected String getObjectKey(Media media, Quality quality) {

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
     * @param media   The {@link Media} whose data is will be referenced by the returned object.
     * @param quality The {@link Quality} of the {@link Media}'s data.
     *
     * @return An {@link S3Object} with metadata and a data stream.
     *
     * @see S3Service#getObject(S3Bucket, String)
     */
    protected S3Object readObject(S3Media media, Quality quality) {

        checkNotNull( media );
        checkNotNull( quality );
        checkState( s3MediaQualityObjects.containsKey( media ) );

        S3Object s3Object = awsService.readObject( getObjectKey( media, quality ) );
        s3MediaQualityObjects.get( media ).put( quality, s3Object );

        return s3Object;
    }

    /**
     * Look up all metadata for media at a certain quality.
     *
     * @param media   The {@link Media} whose data is will be referenced by the returned object.
     * @param quality The {@link Quality} of the {@link Media}'s data.
     *
     * @return An {@link S3Object} with metadata or <code>null</code> if no object exists for the given media at the
     *         given quality.
     *
     * @see S3Service#getObject(S3Bucket, String)
     */
    protected S3Object findObjectDetails(S3Media media, Quality quality) {

        logger.dbg( "%d entries in s3MediaQualityObjects", s3MediaQualityObjects.size() );

        checkNotNull( media );
        checkNotNull( quality );
        checkState( s3MediaQualityObjects.containsKey( media ) );

        Map<Quality, S3Object> s3QualityObjects = s3MediaQualityObjects.get( media );
        S3Object s3Object = s3QualityObjects.get( quality );
        if (s3Object == null) {
            s3Object = awsService.findObjectDetails( getObjectKey( media, quality ) );
            if (s3Object != null) {
                s3QualityObjects.put( quality, s3Object );
            }
        }

        return s3Object;
    }

    /**
     * Get an {@link S3Object} with very basic metadata available.
     *
     * @param media   The {@link Media} whose data is will be referenced by the returned object.
     *
     * @return An {@link S3Object} with basic metadata.
     *
     * @see S3Service#listObjects(S3Bucket)
     */
    protected S3Object getObject(S3Media media) {

        checkNotNull( media );
        checkState( s3MediaQualityObjects.containsKey( media ) );

        return s3MediaQualityObjects.get( media ).get( null );
    }
}
