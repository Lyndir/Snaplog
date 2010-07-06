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

import com.lyndir.lhunath.lib.system.util.DateUtils;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.wayward.i18n.Localized;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Session;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;


/**
 * <h2>{@link MediaTimeFrame}<br> <sub>A time span with an offset that groups a chronological range of media.</sub></h2>
 *
 * <p> <i>Jul 25, 2009</i> </p>
 *
 * @author lhunath
 */
public class MediaTimeFrame implements Localized {

    static final transient DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final Instant offset;
    private final Period range;
    private final Collection<Media> media;

    private transient DateTimeFormatter formatter;

    public MediaTimeFrame(final ReadableInstant offset, final ReadablePeriod range, final Collection<Media> media) {

        this.offset = checkNotNull( offset, "A MediaTimeFrame must have an offset." ).toInstant();
        this.range = checkNotNull( range, "A MediaTimeFrame must have a range." ).toPeriod();
        this.media = checkNotNull( media, "A MediaTimeFrame must have a collection of media." );
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
        if (!ObjectUtils.equal( locale, formatter.getLocale() ))
            formatter = formatter.withLocale( locale );

        return formatter;
    }

    @Override
    public String typeDescription() {

        return msgs.type();
    }

    @Override
    public String objectDescription() {

        return getFormatter().print( offset );
    }

    interface Messages {

        String type();
    }
}
