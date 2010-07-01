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

import com.lyndir.lhunath.lib.system.logging.Logger;
import java.io.Serializable;
import java.util.Collection;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;


/**
 * <h2>{@link MediaTimeFrame}<br> <sub>A time span with an offset that groups a chronological range of media.</sub></h2>
 *
 * <p> <i>Jul 25, 2009</i> </p>
 *
 * @author lhunath
 */
public class MediaTimeFrame implements Serializable {

    private static final Logger logger = Logger.get( MediaTimeFrame.class );

    private final ReadableInstant offset;
    private final ReadablePeriod range;
    private final Collection<Media> media;

    public MediaTimeFrame(final ReadableInstant offset, final ReadablePeriod range, final Collection<Media> media) {

        this.offset = offset;
        this.range = range;
        this.media = media;
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
}
