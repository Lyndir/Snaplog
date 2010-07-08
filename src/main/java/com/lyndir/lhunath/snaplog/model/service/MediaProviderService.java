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
package com.lyndir.lhunath.snaplog.model.service;

import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import java.net.URL;


/**
 * <h2>{@link MediaProviderService}<br> <sub>Services requests on {@link Media} hosted at a certain provider.</sub></h2>
 *
 * <p> <i>Jan 10, 2010</i> </p>
 *
 * @author lhunath
 * @param <A> The type of {@link Album} this {@link MediaProviderService} provides.
 * @param <M> The type of {@link Media} this {@link MediaProviderService} provides.
 */
public interface MediaProviderService<A extends Album, M extends Media> {

    /**
     * Update all media in the given album with what the album's backend provides.
     *
     * @param album The album whose {@link Media} should be updated.
     */
    void loadMedia(A album);

    /**
     * Update all media in the given album with what the album's backend provides.
     *
     * @param album The album whose {@link Media} should be updated.
     */
    void loadMediaData(A album);

    /**
     * Obtain a reference to the resource of media at a certain quality.
     *
     * @param token   Request authentication token should authorize {@link Permission#VIEW} on the media whose URL to return.
     * @param media   The {@link Media} whose resource you want to obtain a reference to.
     * @param quality The {@link Quality} of the {@link Media}'s resource you want to obtain a reference to.
     *
     * @return A {@link URL} which references a resource.
     *
     * @throws PermissionDeniedException When the token does not grant {@link Permission#VIEW} to the media.
     */
    URL findResourceURL(SecurityToken token, M media, Quality quality)
            throws PermissionDeniedException;

    /**
     * Obtain the timestamp at which the given media was created.
     *
     * @param token Request authentication token should authorize {@link Permission#VIEW} on the media whose modification time to return.
     * @param media The media whose creation time you want to obtain.
     *
     * @return A timestamp in milliseconds since the UNIX epoch.
     *
     * @throws PermissionDeniedException When the token does not grant {@link Permission#VIEW} to the media.
     */
    long modifiedTime(SecurityToken token, M media)
            throws PermissionDeniedException;

    /**
     * Create a new {@link Album} with the given metadata.
     *
     * @param ownerUser        The owner of the new album.
     * @param albumName        The name of the new album.
     * @param albumDescription The description of the new album.
     *
     * @return A new {@link Album} instance.
     */
    A newAlbum(User ownerUser, String albumName, String albumDescription);
}
