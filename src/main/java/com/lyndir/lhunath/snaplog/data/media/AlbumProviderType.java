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
package com.lyndir.lhunath.snaplog.data.media;

import com.lyndir.lhunath.snaplog.data.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.model.AWSMediaProviderService;
import com.lyndir.lhunath.snaplog.model.AlbumProvider;


/**
 * <h2>{@link AlbumProviderType}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 11, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public enum AlbumProviderType {

    /**
     * Amazon S3.
     *
     * <p>
     * Provides storage hosted at the Amazon cloud.
     * </p>
     */
    AMAZON_S3( new AlbumProvider<S3Album, S3Media>( S3Album.class, AWSMediaProviderService.class ) );

    private final AlbumProvider<?, ?> albumProvider;


    /**
     * Create a new {@link AlbumProviderType} instance.
     *
     * @param albumProvider The implementation of this AlbumProviderType.
     */
    AlbumProviderType(final AlbumProvider<?, ?> albumProvider) {

        this.albumProvider = albumProvider;
    }

    /**
     * @return The albumProvider of this {@link AlbumProviderType}.
     */
    public AlbumProvider<?, ?> getAlbumProvider() {

        return albumProvider;
    }
}
