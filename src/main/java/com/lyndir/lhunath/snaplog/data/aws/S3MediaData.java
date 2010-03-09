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
package com.lyndir.lhunath.snaplog.data.aws;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jets3t.service.model.S3Object;

import com.google.common.collect.Maps;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.MediaData;
import com.lyndir.lhunath.snaplog.data.Media.Quality;


/**
 * <h2>{@link S3MediaData}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 6, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class S3MediaData extends MediaData<S3Provider, S3Album, S3Media> {

    private Map<Quality, S3Object> s3Objects;


    /**
     * Create a new {@link S3MediaData} instance.
     * 
     * @param media
     *            The {@link S3Media} that we hold data for.
     */
    public S3MediaData(S3Media media) {

        super( media );

        s3Objects = Maps.newHashMapWithExpectedSize( Quality.values().length );
    }

    /**
     * Record an {@link S3Object} for a given {@link Quality} of the {@link Media} we hold data for.
     * 
     * <p>
     * The {@link Quality#METADATA} quality is also updated with this object.
     * </p>
     * 
     * @param quality
     *            The quality of the media that the given s3Object represents.
     * @param s3Object
     *            The S3 data of the media at the given quality.
     */
    public void put(Quality quality, S3Object s3Object) {

        checkNotNull( quality );
        checkNotNull( s3Object );

        s3Objects.put( quality, s3Object );
        s3Objects.put( Quality.METADATA, s3Object );
    }

    /**
     * Retrieve the s3Object for the {@link Media} we hold data for at the given quality.
     * 
     * @param quality
     *            The {@link Quality} at which we want data.
     * @return An S3 data object.
     */
    public S3Object get(Quality quality) {

        checkNotNull( quality );

        return s3Objects.get( quality );
    }
}
