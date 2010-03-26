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

import java.util.Iterator;

import com.db4o.ObjectSet;
import com.google.common.base.Predicate;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.AlbumData;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.User;


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
 * @author lhunath
 */
public interface AlbumService extends MediaProviderService<Album, Media> {

    /**
     * Query for all albums of a user that are visible to a certain user.
     * 
     * @param token
     *            Request authentication token should authorize {@link Permission#VIEW} on the albums to return.
     * @param predicate
     *            The predicate that should evaluate to <code>true</code> for each album to return.
     * 
     * @return An {@link ObjectSet} of albums owned by the given owner that are visible to the given observer.
     */
    ObjectSet<Album> queryAlbums(SecurityToken token, Predicate<Album> predicate);

    /**
     * Look for an album owned by a user.
     * 
     * @param token
     *            Request authentication token should authorize {@link Permission#VIEW} on the album to return.
     * @param ownerUser
     *            The user that owns the album with the given name.
     * @param albumName
     *            The name of the album the user owns.
     * 
     * @return An album, or <code>null</code> if the given user has no album with the given name.
     */
    Album findAlbumWithName(SecurityToken token, User ownerUser, String albumName);

    /**
     * Look for media in the given album.
     * 
     * @param token
     *            Request authentication token should authorize {@link Permission#VIEW} on the album's media to return.
     * @param album
     *            The album to look through.
     * @param mediaName
     *            The name of the media to search for in the album.
     * 
     * @return The media by the given name in the given album or <code>null</code> if no media exists by the given name
     *         in the given album.
     */
    Media findMediaWithName(SecurityToken token, Album album, String mediaName);

    /**
     * Iterate year-based time frames of the media in the given album.
     * 
     * @param token
     *            Request authentication token should authorize {@link Permission#VIEW} on the album's media to return.
     * @param album
     *            The album to generate a list of time frames for.
     * 
     * @return A list of year-type time frames on the given album's media.
     */
    Iterator<MediaTimeFrame> iterateYears(SecurityToken token, Album album);

    /**
     * Register a new {@link Album} owned by the given user whose media is provided by the given provider and with the
     * given metadata.
     * 
     * @param token
     *            Request authentication token should authorize {@link Permission#CONTRIBUTE} on the album owner's
     *            profile.
     * @param album
     *            The new album that should be persisted.
     * @throws PermissionDeniedException
     *             The token does not authorize {@link Permission#CONTRIBUTE} on the album owner's profile.
     */
    void registerAlbum(final SecurityToken token, Album album)
            throws PermissionDeniedException;

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public AlbumData newAlbumData(Album album);
}
