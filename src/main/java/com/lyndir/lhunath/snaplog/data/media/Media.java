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
package com.lyndir.lhunath.snaplog.data.media;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Objects;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.security.AbstractSecureObject;
import com.lyndir.lhunath.snaplog.model.WebUtil;


/**
 * <h2>{@link Media}<br>
 * <sub>DO for .</sub></h2>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class Media extends AbstractSecureObject<Album> implements Comparable<Media>, Serializable {

    static final Logger logger = Logger.get( Media.class );

    private static final DateTimeFormatter filenameFormat = ISODateTimeFormat.basicDateTimeNoMillis();

    private final String name;

    private static final Pattern EXTENSION = Pattern.compile( "\\.[^\\.]*$" );
    private static final Pattern HIDDEN = Pattern.compile( "^\\." );
    private static final Pattern POSTFIX = Pattern.compile( "_.*" );
    private static final Pattern TIMEZONE = Pattern.compile( "[+-]\\d+$" );


    /**
     * @param name
     *            The unique name of this media in the album.
     */
    protected Media(String name) {

        this.name = checkNotNull( name, "Given media name must not be null." );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Album getParent() {

        return getAlbum();
    }

    /**
     * @return The name of this {@link Media}.
     */
    public String getName() {

        return name;
    }

    /**
     * @return The album of this {@link Media}.
     */
    public abstract Album getAlbum();

    /**
     * Obtain the time since the UNIX Epoch in milliseconds since the picture was taken.
     * 
     * @return The amount of milliseconds, or 0 if it could not be determined.
     */
    public long shotTime() {

        String shotTimeString = getName();

        // Trim the extension off the filename.
        shotTimeString = EXTENSION.matcher( shotTimeString ).replaceFirst( "" );

        // Trim the "hidden file prefix" off the filename.
        shotTimeString = HIDDEN.matcher( shotTimeString ).replaceFirst( "" );

        // Trim "_extras" off the filename.
        shotTimeString = POSTFIX.matcher( shotTimeString ).replaceFirst( "" );

        // No time zone == UTC.
        if (!TIMEZONE.matcher( shotTimeString ).matches())
            shotTimeString += "+0000";

        try {
            return filenameFormat.parseMillis( shotTimeString );
        }

        catch (IllegalArgumentException e) {
            logger.wrn( e, "Couldn't parse shot time: %s, for file: %s", shotTimeString, name );

            return 0;
        }
    }

    /**
     * Generate a string to express the time at which the shot was taken; formatted according to the active web
     * session's locale.
     * 
     * @return A date formatted according to the active locale.
     */
    public String getDateString() {

        return DateTimeFormat.mediumDateTime().withLocale( WebUtil.getLocale() ).print( shotTime() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return getDateString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Media o) {

        if (shotTime() > o.shotTime())
            return 1;
        else if (shotTime() < o.shotTime())
            return -1;

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (o instanceof Media)
            return Objects.equal( ((Media) o).getName(), getName() )
                   && Objects.equal( ((Media) o).getAlbum(), getAlbum() );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hashCode( getName(), getAlbum() );
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

        /**
         * The full quality of the original media file.
         */
        METADATA( "metadata", 0, 0, 0 ),

        /**
         * The full quality of the original media file.
         */
        ORIGINAL( "original", -1, -1, 1 ),

        /**
         * Media quality fit for displaying the media such that it fills the screen.
         */
        FULLSCREEN( "fullscreen", 1024, 768, 0.9f ),

        /**
         * Media quality fit for previewing the media at a size where it is easy to tell what it depicts.
         */
        PREVIEW( "preview", 600, 450, 0.8f ),

        /**
         * Media quality fit for giving a hint on what the media is about.
         */
        THUMBNAIL( "thumbnail", 150, 100, 0.75f );

        private final String name;
        private final int maxWidth;
        private final int maxHeight;
        private final float compression;


        Quality(String name, int maxWidth, int maxHeight, float compression) {

            this.name = checkNotNull( name, "Given quality name must not be null." );
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.compression = compression;
        }

        /**
         * @return The identifier used for this quality.
         */
        public String getName() {

            return name;
        }

        /**
         * @return The maximum width media at this quality should have, or <code>-1</code> if there should be no limit.
         */
        public int getMaxWidth() {

            return maxWidth;
        }

        /**
         * @return The maximum height media at this quality should have, or <code>-1</code> if there should be no limit.
         */
        public int getMaxHeight() {

            return maxHeight;
        }

        /**
         * @return The compression ratio media at this quality should use. A decimal number between <code>0</code> and
         *         <code>1</code> (inclusive) where <code>1</code> indicates maximum quality.
         */
        public float getCompression() {

            return compression;
        }

        /**
         * Find the {@link Quality} by the given name.
         * 
         * @param qualityName
         *            The name of the quality (case insensitive) you're after.
         * 
         * @return <code>null</code> if no quality exists for the given name.
         * 
         * @see #getName()
         */
        public static Quality findQualityWithName(String qualityName) {

            for (Quality quality : Quality.values())
                if (quality.getName().equalsIgnoreCase( qualityName ))
                    return quality;

            return null;
        }
    }
}
