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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormat;

import com.google.common.base.Objects;
import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <h2>{@link MediaTimeFrame}<br>
 * <sub>A time span with an offset that groups a chronological range of media.</sub></h2>
 * 
 * <p>
 * {@link MediaTimeFrame}s are spans of time of a certain {@link Type} that span an amount of time defined by the type
 * and are offset by a timestamp of milliseconds since the UNIX epoch.
 * </p>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class MediaTimeFrame implements Comparable<MediaTimeFrame>, Iterable<MediaTimeFrame>, Serializable {

    private static final Logger logger = Logger.get( MediaTimeFrame.class );

    private final MediaTimeFrame parent;
    private final LinkedList<MediaTimeFrame> children;

    private final Type type;
    private final Partial typeTime;

    private final LinkedList<Media> files;


    /**
     * @param parent
     *            The timeframe that contains this one, or <code>null</code> if this timeframe is top-level.
     * @param type
     *            The type of timeframe indicates its time span.
     * @param timeMillis
     *            The time in milliseconds since the UNIX epoch of the beginning of this timeframe.
     */
    public MediaTimeFrame(MediaTimeFrame parent, Type type, long timeMillis) {

        Type parentType = type.findParentType();
        if (parentType == null) {
            if (parent != null)
                throw logger.err( "Type %s permits no parent; given parent type was %s", //
                                  type, parent.type ).toError( IllegalArgumentException.class );
        } else if (parent != null && parent.type != parentType)
            throw logger.err( "Type %s requires parent type %s; given parent type was %s", //
                              type, parentType, parent.type ).toError( IllegalArgumentException.class );

        this.parent = parent;
        children = new LinkedList<MediaTimeFrame>();
        this.type = type;

        typeTime = new Partial( type.getDateType(), new LocalDateTime( timeMillis ).get( type.getDateType() ) );
        files = new LinkedList<Media>();
    }

    /**
     * Get a list of all the media created in this time frame.
     * 
     * @param recurse
     *            <code>true</code>: retrieves all media belonging to this time frame and every time frame that is a
     *            part of it.
     * 
     * @return An unmodifiable list of {@link Media}s.
     */
    public List<Media> getFiles(boolean recurse) {

        List<Media> list = files;
        if (recurse) {
            list = new LinkedList<Media>( files );
            for (MediaTimeFrame childFrame : this)
                list.addAll( childFrame.getFiles( true ) );
        }

        return Collections.unmodifiableList( list );
    }

    /**
     * Add media to this time frame.
     * 
     * @param mediaFile
     *            The media to add to this time frame.
     */
    public void addFile(Media mediaFile) {

        // TODO: Validate that mediaFile is in this time frame.
        files.add( mediaFile );
    }

    /**
     * @return The beginning of this time frame.
     */
    public LocalDate getTime() {

        if (parent == null)
            return new LocalDate( 0 ).withFields( typeTime );

        return new LocalDate( 0 ).withFields( parent.getTime() ).withFields( typeTime );
    }

    /**
     * @param instantMillis
     *            An amount of milliseconds since the UNIX epoch.
     * 
     * @return <code>true</code> if the given point in time lays within this timeframe.
     */
    public boolean containsTime(long instantMillis) {

        long begin = getTime().toDateMidnight().getMillis();
        Interval interval = new Interval( begin, type.getDateType().getField( null ).add( begin, 1 ) );

        return interval.contains( instantMillis );
    }

    /**
     * @return A short representation of this time frame.
     */
    public String getShortName() {

        return DateTimeFormat.forPattern( type.getDateFormatString() ).print( typeTime );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<MediaTimeFrame> iterator() {

        return children.iterator();
    }

    /**
     * @param mediaTimeFrame
     *            The child {@link MediaTimeFrame} to add to this one.
     */
    public void addTimeFrame(MediaTimeFrame mediaTimeFrame) {

        Type childType = type.findChildType();
        if (childType == null || childType != mediaTimeFrame.type)
            throw logger.err( "This timeframe (type: %s) doesn't support children of type: %s (supports: %s)", //
                              type, mediaTimeFrame.type, childType ).toError( IllegalArgumentException.class );

        children.add( mediaTimeFrame );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return getShortName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(MediaTimeFrame o) {

        return typeTime.compareTo( o.typeTime );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (obj instanceof MediaTimeFrame)
            return ((MediaTimeFrame) obj).type == type && ((MediaTimeFrame) obj).typeTime.equals( typeTime );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hashCode( type, typeTime );
    }


    /**
     * <h2>{@link Type}<br>
     * <sub>[in short] (TODO).</sub></h2>
     * 
     * <p>
     * <i>Jan 28, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    public enum Type {

        /**
         * One calendar year.
         */
        YEAR( DateTimeFieldType.year(), null, "yyyy" ),

        /**
         * One calendar month.
         */
        MONTH( DateTimeFieldType.monthOfYear(), YEAR, "MMM" ),

        /**
         * One calendar day.
         */
        DAY( DateTimeFieldType.dayOfMonth(), MONTH, "dd" );

        private final DateTimeFieldType dateType;
        private final Type parentType;
        private final String dateFormatString;


        Type(DateTimeFieldType dateType, Type parentType, String dateFormatString) {

            this.dateType = dateType;
            this.parentType = parentType;
            this.dateFormatString = dateFormatString;
        }

        /**
         * @return The type of date this time frame represents.
         */
        public DateTimeFieldType getDateType() {

            return dateType;
        }

        /**
         * @return The type of time frame our parent can be.
         */
        public Type findParentType() {

            return parentType;
        }

        /**
         * @return The type of time frame our children can be.
         */
        public Type findChildType() {

            for (Type type : Type.values())
                if (this == type.findParentType())
                    return type;

            return null;
        }

        /**
         * @return The string that formats timestamps of this type.
         */
        public String getDateFormatString() {

            return dateFormatString;
        }
    }
}
