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
package com.lyndir.lhunath.snaplog.data.object.media;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.DateUtils;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.system.i18n.Localized;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Session;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;


/**
 * <h2>{@link TimeFrame}<br> <sub>A time span with an offset that groups a chronological range of media.</sub></h2>
 *
 * <p> <i>Jul 25, 2009</i> </p>
 *
 * @author lhunath
 */
public class TimeFrame implements Localized, Comparable<TimeFrame> {

    static final Logger logger = Logger.get( TimeFrame.class );

    static final DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final ReadableInstant offset;
    private final ReadablePeriod range;
    private final Collection<Media> media;

    private transient DateTimeFormatter formatter;

    public TimeFrame(final ReadableInstant offset, final ReadablePeriod range, final Collection<Media> media) {

        this.offset = checkNotNull( offset, "A TimeFrame must have an offset." );
        this.range = checkNotNull( range, "A TimeFrame must have a range." );
        this.media = checkNotNull( media, "A TimeFrame must have a collection of media." );
    }

    public ReadableInstant getOffset() {

        return offset;
    }

    public ReadablePeriod getRange() {

        return range;
    }

    public Collection<Media> getMedia() {

        return media;
    }

    private DateTimeFormatter getFormatter() {

        if (formatter == null) {
            // Find all the fields to include in the formatter.
            DurationFieldType[] fields = range.toPeriod().getFieldTypes();
            DurationFieldType smallestField = fields[fields.length - 1];
            List<DateTimeFieldType> types = DateUtils.fieldsFrom( DateUtils.convert( smallestField ) );

            // Build a formatter from the fields.
            formatterBuilder.clear();
            for (Iterator<DateTimeFieldType> it = types.iterator(); it.hasNext();) {
                DateTimeFieldType type = it.next();

                formatterBuilder.appendShortText( type );
                if (it.hasNext())
                    formatterBuilder.appendLiteral( ' ' );
            }
            formatter = formatterBuilder.toFormatter();
        }

        // Switch formatter to the active locale.
        Locale locale = Session.exists()? Session.get().getLocale(): null;
        if (!ObjectUtils.isEqual( locale, formatter.getLocale() ))
            formatter = formatter.withLocale( locale );

        return formatter;
    }

    @Override
    public int compareTo(final TimeFrame o) {

        return offset.compareTo( o.offset );
    }

    @Override
    public int hashCode() {

        return Objects.hashCode( getOffset(), getRange() );
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;
        if (!getClass().isInstance( obj ))
            return false;

        TimeFrame mtfObj = (TimeFrame) obj;

        return Objects.equal( getOffset(), mtfObj.getOffset() ) && Objects.equal( getRange(), mtfObj.getRange() );
    }

    @Override
    public String getLocalizedType() {

        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {

        String s = getFormatter().print( offset );
        logger.dbg( "frame: %s, starts with: %s, ends with: %s - desc: %s", this, media.iterator().next(),
                    Iterators.getLast( media.iterator() ), s );
        return s;
    }

    @Override
    public String toString() {

        return String.format( "{frame: offset=%s, range=%s, media:%d}", offset, range, media.size() );
    }

    private void readObject(final ObjectInputStream stream)
            throws IOException, ClassNotFoundException {

        // Default deserialization.
        stream.defaultReadObject();

        // Manually load a new Messages proxy.
        MessagesFactory.initialize( this, Messages.class );
    }

    interface Messages {

        String type();
    }
}
