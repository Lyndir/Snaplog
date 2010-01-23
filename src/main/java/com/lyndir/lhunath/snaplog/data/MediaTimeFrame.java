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
package com.lyndir.lhunath.snaplog.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.lyndir.lhunath.lib.system.logging.Logger;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;


/**
 * <h2>{@link MediaTimeFrame}<br>
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
public class MediaTimeFrame extends LinkedList<MediaTimeFrame> implements Comparable<MediaTimeFrame> {

    private static final Logger logger = Logger.get( MediaTimeFrame.class );

    private MediaTimeFrame parent;
    private Type type;
    private Partial typeTime;

    private LinkedList<Media> files;


    public MediaTimeFrame(MediaTimeFrame parent, Type type, long timeMillis) {

        if (type.getParentType() == null) {
            if (parent != null) {
                logger.err( "Type %s permits no parent; given parent type was %s", //
                            type, parent.type );
                throw logger.toError( IllegalArgumentException.class );
            }
        } else if (parent != null && parent.type != type.getParentType()) {
            logger.err( "Type %s requires parent type %s; given parent type was %s", //
                        type, type.getParentType(), parent.type );
            throw logger.toError( IllegalArgumentException.class );
        }

        this.parent = parent;
        this.type = type;

        typeTime = new Partial( type.getDateType(), new LocalDateTime( timeMillis ).get( type.getDateType() ) );
        files = new LinkedList<Media>();
    }

    /**
     * Get a list of all the media created in this time frame.
     *
     * @param recurse <code>true</code>: retrieves all media belonging to this time frame and every time frame that is a
     *                part of it.
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

    public void addFile(Media mediaFile) {

        files.add( mediaFile );
    }

    public LocalDate getTime() {

        if (parent == null)
            return new LocalDate( 0 ).withFields( typeTime );

        return new LocalDate( 0 ).withFields( parent.getTime() ).withFields( typeTime );
    }

    public boolean containsTime(long instantMillis) {

        long begin = getTime().toDateMidnight().getMillis();
        Interval interval = new Interval( begin, type.getDateType().getField( null ).add( begin, 1 ) );

        return interval.contains( instantMillis );
    }

    public String getShortName() {

        return DateTimeFormat.forPattern( type.getDateFormatString() ).print( typeTime );
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
    public int compareTo(MediaTimeFrame o) {

        return typeTime.compareTo( o.typeTime );
    }


    public enum Type {

        YEAR( DateTimeFieldType.year(), null, "yyyy" ),
        MONTH( DateTimeFieldType.monthOfYear(), Type.YEAR, "MMM" ),
        DAY( DateTimeFieldType.dayOfMonth(), Type.MONTH, "dd" );

        private DateTimeFieldType dateType;
        private Type parentType;
        private String dateFormatString;


        private Type(DateTimeFieldType dateType, Type parentType, String dateFormatString) {

            this.dateType = dateType;
            this.parentType = parentType;
            this.dateFormatString = dateFormatString;
        }

        public DateTimeFieldType getDateType() {

            return dateType;
        }

        public Type getParentType() {

            return parentType;
        }

        public String getDateFormatString() {

            return dateFormatString;
        }
    }

}
