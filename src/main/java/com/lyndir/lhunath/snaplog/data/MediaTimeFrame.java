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
public class MediaTimeFrame implements Comparable<MediaTimeFrame>, Iterable<MediaTimeFrame> {

    private static final Logger              logger = Logger.get( MediaTimeFrame.class );

    private final MediaTimeFrame             parent;
    private final LinkedList<MediaTimeFrame> children;

    private final Type                       type;
    private final Partial                    typeTime;

    private final LinkedList<Media>          files;


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
    @Override
    public int compareTo(MediaTimeFrame o) {

        return typeTime.compareTo( o.typeTime );
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (obj instanceof MediaTimeFrame)
            return ((MediaTimeFrame) obj).type == type && ((MediaTimeFrame) obj).typeTime.equals( typeTime );

        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode( type, typeTime );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<MediaTimeFrame> iterator() {

        return children.iterator();
    }


    public enum Type {

        YEAR(DateTimeFieldType.year(), null, "yyyy"),
        MONTH(DateTimeFieldType.monthOfYear(), YEAR, "MMM"),
        DAY(DateTimeFieldType.dayOfMonth(), MONTH, "dd");

        private final DateTimeFieldType dateType;
        private final Type              parentType;
        private final String            dateFormatString;


        Type(DateTimeFieldType dateType, Type parentType, String dateFormatString) {

            this.dateType = dateType;
            this.parentType = parentType;
            this.dateFormatString = dateFormatString;
        }

        public DateTimeFieldType getDateType() {

            return dateType;
        }

        public Type findParentType() {

            return parentType;
        }

        public Type findChildType() {

            for (Type type : Type.values())
                if (this.equals( type.findParentType() ))
                    return type;

            return null;
        }

        public String getDateFormatString() {

            return dateFormatString;
        }
    }


    /**
     * @param mediaTimeFrame
     *            The child {@link MediaTimeFrame} to add to this one.
     */
    public void addTimeFrame(MediaTimeFrame mediaTimeFrame) {

        Type childType = type.findChildType();
        if (childType == null || !childType.equals( mediaTimeFrame.type ))
            throw logger.err( "This timeframe (type: %s) doesn't support children of type: %s (supports: %s)", //
                    type, mediaTimeFrame.type, childType ).toError( IllegalArgumentException.class );

        children.add( mediaTimeFrame );
    }
}
