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

import com.db4o.ObjectSet;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.user.LinkID;
import com.lyndir.lhunath.snaplog.data.user.User;


/**
 * <h2>{@link UserService}<br>
 * <sub>Service to manage and access {@link User}s.</sub></h2>
 * 
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public interface UserService {

    /**
     * Create a new user with the given userName and linkID identifier.
     * 
     * @param linkID
     *            The linkID identifier that linkID uses to identify user they authenticated to us.
     * @param userName
     *            The unique userName the user would like to use as his snaplog identifier.
     * 
     * @return A newly registered user.
     */
    User registerUser(LinkID linkID, String userName);

    /**
     * Find the existing user registered with the given linkID identifier.
     * 
     * @param linkID
     *            The linkID identifier that the user has registered with.
     * 
     * @return The user with the given linkID identifier or <code>null</code> if no such user exists yet.
     */
    User findUserWithLinkID(LinkID linkID);

    /**
     * Find the existing user registered with the given userName.
     * 
     * @param userName
     *            The userName the user has registered with.
     * 
     * @return The user with the given userName or <code>null</code> if no such user exists yet.
     */
    User findUserWithUserName(String userName);

    /**
     * Query for all albums of a user that are visible to a certain user.
     * 
     * @param ownerUser
     *            The {@link User} that owns the returned {@link Album}s.
     * @param observerUser
     *            The {@link User} that wants to see the list of {@link Album}s. If <code>null</code>, only publicly
     *            accessible albums are returned.
     * 
     * @return An {@link ObjectSet} of albums owned by the given owner that are visible to the given observer.
     */
    ObjectSet<Album> queryAlbumsOfUserVisibleToUser(User ownerUser, User observerUser);

    /**
     * TODO: Limit to set of users current user has a relation with.
     * 
     * @return An {@link ObjectSet} of all known {@link User}s.
     */
    ObjectSet<User> queryUsers();
}
