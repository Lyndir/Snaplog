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

import java.util.List;

import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.data.User;


/**
 * <h2>{@link AlbumService}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that provides the resources for the media in this time frame.
 * @author lhunath
 */
public interface AlbumService<P extends Provider> extends MediaProviderService<P, Album<P>, Media<P>> {

    /**
     * Look for an album owned by a user.
     * 
     * @param user
     *            The user that owns the album with the given name.
     * @param albumName
     *            The name of the album the user owns.
     * 
     * @return An album, or <code>null</code> if the given user has no album with the given name.
     */
    Album<P> findAlbumWithName(User user, String albumName);

    /**
     * Look for media in the given album.
     * 
     * @param album
     *            The album to look through.
     * @param mediaName
     *            The name of the media to search for in the album.
     * 
     * @return The media by the given name in the given album or <code>null</code> if no media exists by the given name
     *         in the given album.
     */
    Media<P> findMediaWithName(Album<P> album, String mediaName);

    /**
     * Obtain a list of year-based time frames of the media in the given album.
     * 
     * @param album
     *            The album to generate a list of time frames for.
     * 
     * @return A list of year-type time frames on the given album's media.
     */
    List<MediaTimeFrame<P>> getYears(Album<P> album);
}
