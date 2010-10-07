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

import com.db4o.ObjectSet;
import com.google.common.base.Predicate;
import com.lyndir.lhunath.lib.system.collection.SizedListIterator;
import com.lyndir.lhunath.lib.wayward.model.WicketInjected;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.error.UsernameTakenException;


/**
 * <h2>{@link UserService}<br> <sub>Service to manage and access {@link User}s.</sub></h2>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public interface UserService extends WicketInjected {

    /**
     * Create a new user with the given userName and linkID identifier.
     *
     * @param linkID   The linkID identifier that linkID uses to identify user they authenticated to us.
     * @param userName The unique userName the user would like to use as his snaplog identifier.
     *
     * @return A newly registered user.
     *
     * @throws UsernameTakenException
     *          When the given userName is already taken.
     */
    User registerUser(LinkID linkID, String userName)
            throws UsernameTakenException;

    /**
     * Find the existing user registered with the given linkID identifier.
     *
     * @param linkID The linkID identifier that the user has registered with.
     *
     * @return The user with the given linkID identifier or <code>null</code> if no such user exists yet.
     */
    User findUserWithLinkID(LinkID linkID);

    /**
     * Find the existing user registered with the given userName.
     *
     * @param userName The userName the user has registered with.
     *
     * @return The user with the given userName or <code>null</code> if no such user exists yet.
     */
    User findUserWithUserName(String userName);

    /**
     * Get the existing user registered with the given userName.
     *
     * @param userName The userName the user has registered with.
     *
     * @return The user with the given userName.
     *
     * @throws UserNotFoundException No user exists with the given userName.
     */
    User getUserWithUserName(String userName)
            throws UserNotFoundException;

    /**
     * @param predicate An optional predicate that should evaluate to <code>true</code> for each user to return. If <code>null</code>, all
     *                  users implicitly match.
     *
     * @return An {@link ObjectSet} of the {@link User}s that apply to the given predicate and are viewable using the given token.
     */
    SizedListIterator<User> iterateUsers(Predicate<User> predicate);

    /**
     * @param token Request authentication token should authorize {@link Permission#VIEW} on the user's profile.
     * @param user  The user whose profile is requested.
     *
     * @return The user's profile.
     *
     * @throws PermissionDeniedException When the given token does not authorize access to the given user's profile.
     */
    UserProfile getProfile(SecurityToken token, User user)
            throws PermissionDeniedException;

    /**
     * @param token Request authentication token should authorize {@link Permission#VIEW} on the user's profile.
     * @param user  The user whose profile access is being checked for.
     *
     * @return <code>true</code>: The token authorizes {@link Permission#VIEW} access on the given profile.
     */
    boolean hasProfileAccess(SecurityToken token, User user);
}
