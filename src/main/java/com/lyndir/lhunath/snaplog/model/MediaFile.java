/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.snaplog.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.apache.wicket.Application;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.SharedResources;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.lyndir.lhunath.lib.system.Utils;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.util.ImageUtils;


/**
 * <h2>{@link MediaFile}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class MediaFile implements Serializable, Comparable<MediaFile> {

    static final Logger                    logger         = Logger.get( MediaFile.class );

    private static final DateTimeFormatter filenameFormat = ISODateTimeFormat.basicDateTimeNoMillis();

    private ImmutableMap<Quality, String>  referenceNames;

    private File                           file;


    public MediaFile(final File file) {

        this.file = file;

        SharedResources sharedResources = Application.get().getSharedResources();

        Builder<Quality, String> referenceNamesBuilder = new ImmutableMap.Builder<Quality, String>();
        for (final Quality quality : Quality.values()) {
            // TODO: Find a more unique resource reference name.
            final String referenceName = String.format( "%s.%s", file.getName(), quality.getName() );
            referenceNamesBuilder.put( quality, referenceName );

            sharedResources.add( referenceName, new DynamicWebResource( file.getName() ) {

                @Override
                protected ResourceState getResourceState() {

                    return new ResourceState() {

                        @Override
                        public byte[] getData() {

                            try {
                                ByteArrayOutputStream imageDataStream = new ByteArrayOutputStream();
                                BufferedImage image = ImageIO.read( file );

                                switch (quality) {
                                    case ORIGINAL:
                                        return Utils.readStream( new FileInputStream( file ) );

                                    case SIZED:
                                        ImageUtils.write( ImageUtils.rescale( image, 600, 450 ), //
                                                          imageDataStream, "image/jpeg", 1.0f, true );

                                        return imageDataStream.toByteArray();

                                    case THUMBNAIL:
                                        ImageUtils.write( ImageUtils.rescale( image, 150, 100 ), //
                                                          imageDataStream, "image/jpeg", 1.0f, true );

                                        return imageDataStream.toByteArray();
                                }

                                throw logger.bug( "Unsupported quality: %d", quality )
                                            .toError( UnsupportedOperationException.class );
                            }

                            catch (FileNotFoundException e) {
                                throw logger.err( e, "Image file could not be found: %s", file ).toError();
                            } catch (IOException e) {
                                throw logger.err( e, "Image file could not be read: %s", file ).toError();
                            }
                        }

                        @Override
                        public String getContentType() {

                            return "image/jpeg";
                        }
                    };
                }
            } );
        }
        referenceNames = referenceNamesBuilder.build();
    }

    /**
     * @param quality
     *            The quality at which to view this {@link MediaFile}.
     * @return A reference to this file resource.
     */
    public ResourceReference newResourceReference(Quality quality) {

        return new ResourceReference( referenceNames.get( quality ) );
    }

    /**
     * @return The on-disk file.
     */
    public File getFile() {

        return file;
    }

    public long shotTime() {

        String shotTimeString = file.getName();

        // Trim the extension off the filename.
        shotTimeString = shotTimeString.replaceFirst( "\\.[^\\.]*$", "" );

        // Trim the "hidden file prefix" off the filename.
        shotTimeString = shotTimeString.replaceFirst( "^\\.", "" );

        // Trim "_extras" off the filename.
        shotTimeString = shotTimeString.replaceFirst( "_.*", "" );

        // No time zone == UTC.
        if (!shotTimeString.matches( "[+-]\\d+$" ))
            shotTimeString += "+0000";

        try {
            return filenameFormat.parseMillis( shotTimeString );
        }

        catch (IllegalArgumentException e) {
            logger.wrn( e, "Couldn't parse shot time: %s for file: %s", shotTimeString, file.getName() );

            return file.lastModified();
        }
    }

    public long modifiedTime() {

        return file.lastModified();
    }

    public String getDateString() {

        return DateTimeFormat.mediumDateTime().withLocale( WebUtil.getLocale() ).print( shotTime() );
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(MediaFile o) {

        return (int) ((shotTime() - o.shotTime()) / 1000);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return getDateString() + ":" + shotTime();
    }


    /**
     * <h2>{@link Quality}<br>
     * <sub>The media resource is available at different {@link Quality} levels.</sub></h2>
     * 
     * <p>
     * <i>Jan 6, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    public enum Quality {
        ORIGINAL("orig"),
        SIZED("sized"),
        THUMBNAIL("thumb");

        private String name;


        private Quality(String name) {

            this.name = name;
        }

        public String getName() {

            return name;
        }
    }
}
