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
package com.lyndir.lhunath.snaplog.data.media.aws;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.media.MediaData;
import java.util.Map;
import org.jets3t.service.model.S3Object;


/**
 * <h2>{@link S3MediaData}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 6, 2010</i> </p>
 *
 * @author lhunath
 */
public class S3MediaData extends MediaData {

    private final S3Media media;
    private final Map<Quality, S3Object> s3Objects;

    /**
     * Create a new {@link S3MediaData} instance.
     *
     * @param media          The {@link S3Media} that we hold data for.
     * @param metaDataObject The {@link S3Object} that contains the metadata for the media.
     */
    public S3MediaData(final S3Media media, final S3Object metaDataObject) {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( metaDataObject, "Given S3 object must not be null." );
        checkArgument( metaDataObject.isMetadataComplete(), "Given S3 object does not have complete metadata." );

        this.media = media;

        s3Objects = Maps.newHashMapWithExpectedSize( Quality.values().length );
        s3Objects.put( Quality.METADATA, metaDataObject );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Media getMedia() {

        return media;
    }

    /**
     * Record an {@link S3Object} for a given {@link Quality} of the {@link Media} we hold data for.
     *
     * <p> The {@link Quality#METADATA} quality is also updated with this object. </p>
     *
     * @param quality  The quality of the media that the given s3Object represents.
     * @param s3Object The S3 data of the media at the given quality.
     *
     * @return <code>this</code> for chaining.
     */
    public MediaData put(final Quality quality, final S3Object s3Object) {

        checkNotNull( quality, "Given quality must not be null." );
        checkNotNull( s3Object, "Given S3 object must not be null." );

        if (quality != Quality.METADATA)
            // If quality is METADATA, the next step will take care of it.
            s3Objects.put( quality, s3Object );

        if (s3Object.isMetadataComplete())
            s3Objects.put( Quality.METADATA, s3Object );
        else
            // Tried to store an object under quality of METADATA while it doesn't have complete metadata.
            checkArgument( quality != Quality.METADATA, "Can't store METADATA on given S3 object since it does not have complete metadata." );

        return this;
    }

    /**
     * Retrieve the s3Object for the {@link Media} we hold data for at the given quality.
     *
     * @param quality The {@link Quality} at which we want data.
     *
     * @return An S3 data object.
     */
    public S3Object get(final Quality quality) {

        checkNotNull( quality, "Given quality must not be null." );

        return s3Objects.get( quality );
    }
}
