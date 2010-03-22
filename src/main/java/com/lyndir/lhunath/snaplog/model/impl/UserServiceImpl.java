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
package com.lyndir.lhunath.snaplog.model.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.util.SafeObjects;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.LinkID;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.error.UsernameTakenException;
import com.lyndir.lhunath.snaplog.model.SecurityService;
import com.lyndir.lhunath.snaplog.model.UserService;


/**
 * <h2>{@link UserServiceImpl}<br>
 * 
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class UserServiceImpl implements UserService {

    ObjectContainer db;
    SecurityService securityService;


    /**
     * @param db
     *            See {@link ServicesModule}.
     * @param securityService
     *            See {@link ServicesModule}.
     */
    @Inject
    public UserServiceImpl(ObjectContainer db, SecurityService securityService) {

        this.db = db;
        this.securityService = securityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(LinkID linkID, String userName)
            throws UsernameTakenException {

        checkNotNull( linkID, "Given linkID must not be null." );
        checkNotNull( userName, "Given userName must not be null." );

        if (findUserWithUserName( SecurityToken.INTERNAL_USE_ONLY, userName ) != null)
            throw new UsernameTakenException( userName );

        User user = new User( linkID, userName );
        db.store( user );

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserWithLinkID(final LinkID linkID) {

        checkNotNull( linkID, "Given linkID must not be null." );

        ObjectSet<User> userQuery = db.query( new Predicate<User>() {

            @Override
            public boolean match(User candidate) {

                return SafeObjects.equal( candidate.getLinkID(), linkID );
            }
        } );
        if (userQuery.hasNext())
            return userQuery.next();

        // No user exists yet for given linkID.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserWithUserName(final SecurityToken token, final String userName) {

        checkNotNull( userName, "Given userName must not be null." );

        ObjectSet<User> userQuery = db.query( new Predicate<User>() {

            @Override
            public boolean match(User candidate) {

                return SafeObjects.equal( candidate.getUserName(), userName )
                       && securityService.hasAccess( Permission.VIEW, token, candidate );
            }
        } );
        if (userQuery.hasNext())
            return userQuery.next();

        // No user exists yet for given userName.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectSet<Album> queryAlbumsOfUser(final SecurityToken token, final User ownerUser) {

        checkNotNull( ownerUser, "Given owner user must not be null." );

        return db.query( new Predicate<Album>() {

            @Override
            public boolean match(Album candidate) {

                return SafeObjects.equal( candidate.getOwnerUser(), ownerUser )
                       && securityService.hasAccess( Permission.VIEW, token, candidate );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectSet<User> queryUsers(final SecurityToken token) {

        return db.query( new Predicate<User>() {

            @Override
            public boolean match(User candidate) {

                return securityService.hasAccess( Permission.VIEW, token, candidate );
            }
        } );
    }
}
