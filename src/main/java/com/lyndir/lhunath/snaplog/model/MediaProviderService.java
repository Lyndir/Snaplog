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
package com.lyndir.lhunath.snaplog.model;

import java.net.URI;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Media.Quality;


/**
 * <h2>{@link MediaProviderService}<br>
 * <sub>Services requests on {@link Media} hosted at a certain provider.</sub></h2>
 * 
 * <p>
 * <i>Jan 10, 2010</i>
 * </p>
 * 
 * @param <A>
 *            The type of {@link Album} supported by this {@link MediaProviderService}.
 * @param <M>
 *            The type of {@link Media} supported by this {@link MediaProviderService}.
 * 
 * @author lhunath
 */
public interface MediaProviderService<A extends Album, M extends Media> {

    /**
     * Enumerate all media in a certain album.
     * 
     * @param album
     *            The album whose {@link Media} you want to enumerate.
     * @return All the {@link Media} from the given {@link Album}.
     */
    ImmutableList<? extends Media> getFiles(A album);

    /**
     * Obtain a reference to the resource of media at a certain quality.
     * 
     * @param media
     *            The {@link Media} whose resource you want to obtain a reference to.
     * @param quality
     *            The {@link Quality} of the {@link Media}'s resource you want to obtain a reference to.
     * @return A {@link URI} which references a resource.
     */
    public URI getResourceURI(M media, Quality quality);

    /**
     * Obtain the timestamp at which the given media was created.
     * 
     * @param media
     *            The media whose creation time you want to obtain.
     * @return A timestamp in milliseconds since the UNIX epoch.
     */
    public long modifiedTime(M media);
}
