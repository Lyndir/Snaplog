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

import com.google.common.base.Predicate;
import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.snaplog.security.SSecurityToken;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.opal.wayward.model.WicketInjected;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import java.net.URL;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * <h2>{@link SourceService}<br> <sub>Services requests on {@link Media} hosted at a certain provider.</sub></h2>
 *
 * <p> <i>Jan 10, 2010</i> </p>
 *
 * @param <S> The type of {@link Source} this {@link SourceService} provides.
 * @param <M> The type of {@link Media} this {@link SourceService} provides.
 *
 * @author lhunath
 */
public interface SourceService<S extends Source, M extends Media> extends WicketInjected {

    /**
     * Update all media from the given source with what the backend provides.
     *
     * @param token     Request authentication token should authorize {@link Permission#ADMINISTER} on the source to load media from.
     * @param source The source where {@link Media} should be loaded from.
     *
     * @throws PermissionDeniedException When the token does not grant {@link Permission#ADMINISTER} to the source.
     */
    void loadMedia(SSecurityToken token, S source)
            throws PermissionDeniedException;

    /**
     * Update all media from the given source with what the backend provides.
     *
     * @param token     Request authentication token should authorize {@link Permission#ADMINISTER} on the source to load media data from.
     * @param source The source where {@link MediaData} should be loaded from.
     *
     * @throws PermissionDeniedException When the token does not grant {@link Permission#ADMINISTER} to the source.
     */
    void loadMediaData(SSecurityToken token, S source)
            throws PermissionDeniedException;

    /**
     * @param token     Request authentication token should authorize {@link Permission#VIEW} on the sources to return.
     * @param predicate The predicate should evaluate to <code>true</code> for all desired sources and <code>false</code> for undesired
     *                  sources.
     *
     * @return An Iterator of sources that are viewable with the given token and are selected by the given predicate.
     */
    Iterator<Source> iterateSources(SSecurityToken token, Predicate<Source> predicate);

    /**
     * @param token     Request authentication token should authorize {@link Permission#VIEW} on the source's media to return.
     * @param source    The source to retrieve media from.
     * @param ascending Whether the first media returned should be the earliest (<code>true</code>) or latest (<code>false</code>).
     *
     * @return An {@link Iterator} of media in the given source that are visible to the given observer.
     */
    ListIterator<M> iterateMedia(SSecurityToken token, S source, boolean ascending);

    /**
     * Look for media in the given source.
     *
     * @param token     Request authentication token should authorize {@link Permission#VIEW} on the source's media to return.
     * @param owner     The user that owns the named media.
     * @param mediaName The name of the media to search for in the source.
     *
     * @return The media by the given name in the given source or <code>null</code> if no media exists by the given name in the given
     *         source.
     */
    Media findMediaWithName(SSecurityToken token, User owner, String mediaName);

    /**
     * Create a new public mapping for the given media.
     *
     * @param token Request authentication token should authorize {@link Permission#ADMINISTER} on the given media.
     * @param media The media that should be made publicly available through a new mapping.
     *
     * @return The URL that provides public access to the given media using the mapping's access control.
     *
     * @throws PermissionDeniedException When the token does not grant {@link Permission#ADMINISTER} to the media.
     */
    MediaMapping newMapping(SSecurityToken token, M media)
            throws PermissionDeniedException;

    /**
     * Find the media mapping that maps the given mapping string.
     *
     * @param token   Request authentication token should authorize {@link Permission#VIEW} on the given media mapping.
     * @param mapping The mapping string that identifies the mapping to return.
     *
     * @return The media mapping for the given mapping string or <code>null</code> if no such mapping could be found or the token does not
     *         authorize permission to view it.
     */
    MediaMapping findMediaMapping(SSecurityToken token, String mapping);

    /**
     * Obtain a reference to the given media's resource of a given quality.
     *
     * @param token   Request authentication token should authorize {@link Permission#VIEW} on the media whose URL to return.
     * @param media   The {@link Media} whose resource you want to obtain a reference to.
     * @param quality The {@link Quality} of the {@link Media}'s resource you want to obtain a reference to.
     *
     * @return A {@link URL} which references a resource.
     *
     * @throws PermissionDeniedException When the token does not grant {@link Permission#VIEW} to the media.
     */
    URL findResourceURL(SSecurityToken token, M media, Quality quality)
            throws PermissionDeniedException;

    /**
     * Mark the given media as deleted.
     *
     * @param token Request authentication token should authorize {@link Permission#ADMINISTER} on the media whose URL to return.
     * @param media The media that needs to be deleted.
     *
     * @throws PermissionDeniedException When the token does not grant {@link Permission#ADMINISTER} to the media.
     */
    void delete(SSecurityToken token, M media)
            throws PermissionDeniedException;

    /**
     * Persists a new {@link Source}.
     *
     * @param token  Request authentication token should authorize {@link Permission#ADMINISTER} on the owner of the new source.
     * @param source The new source to persist.
     *
     * @return The {@link Source}, persisted and ready for use.
     */
    S newSource(SSecurityToken token, S source);
}
