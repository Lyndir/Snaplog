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
package com.lyndir.lhunath.snaplog.data.object.media.aws;

import static com.google.common.base.Preconditions.checkNotNull;

import com.lyndir.lhunath.snaplog.data.object.media.Media;


/**
 * <h2>{@link S3Media}<br> <sub>{@link Media} whose data is provided by Amazon's S3.</sub></h2>
 *
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public class S3Media extends Media {

    private final S3Album album;

    /**
     * @param album The album to which this media belongs.
     * @param name  The unique name of this media in the album.
     */
    public S3Media(final S3Album album, final String name) {

        super( name );

        this.album = checkNotNull( album, "Given album must not be null." );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Album getAlbum() {

        return album;
    }
}
