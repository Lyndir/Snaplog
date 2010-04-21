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
import static com.google.common.base.Preconditions.checkState;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.util.SafeObjects;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.snaplog.data.security.ACL;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.LinkID;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.data.user.UserProfile;
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

    final ObjectContainer db;
    final SecurityService securityService;


    /**
     * @param db              See {@link ServicesModule}.
     * @param securityService See {@link ServicesModule}.
     */
    @Inject
    public UserServiceImpl(final ObjectContainer db, final SecurityService securityService) {

        this.db = db;
        this.securityService = securityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(final LinkID linkID, final String userName)
            throws UsernameTakenException {

        checkNotNull( linkID, "Given linkID must not be null." );
        checkNotNull( userName, "Given userName must not be null." );

        if (findUserWithUserName( userName ) != null)
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

        ObjectSet<User> userQuery = queryUsers( new IPredicate<User>() {

            @Override
            public boolean apply(final User input) {

                return input != null && SafeObjects.equal( input.getLinkID(), linkID );
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
    public User findUserWithUserName(final String userName) {

        checkNotNull( userName, "Given userName must not be null." );

        ObjectSet<User> userQuery = queryUsers( new IPredicate<User>() {

            @Override
            public boolean apply(final User input) {

                return input != null && SafeObjects.equal( input.getUserName(), userName );
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
    public ObjectSet<User> queryUsers(final com.google.common.base.Predicate<User> predicate) {

        return db.query( new Predicate<User>() {

            @Override
            public boolean match(final User candidate) {

                // Never allow this user to be queried; it is purely for internal use only.
                if (SafeObjects.equal( candidate, ACL.DEFAULT ))
                    return false;

                com.google.common.base.Predicate<User> predicate_ = predicate;
                if (predicate_ == null)
                    predicate_ = Predicates.alwaysTrue();

                return predicate_.apply( candidate );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getProfile(final SecurityToken token, final User user)
            throws PermissionDeniedException {

        checkNotNull( user, "Given user must not be null." );

        ObjectSet<UserProfile> profileQuery = db.query( new Predicate<UserProfile>() {

            @Override
            public boolean match(final UserProfile candidate) {

                return candidate != null && candidate.getUser().equals( user );
            }
        } );

        checkState( profileQuery.hasNext(), "User %s has no profile.", user );

        UserProfile profile = profileQuery.next();
        securityService.assertAccess( Permission.VIEW, token, profile );

        return profile;
    }

    @Override
    public boolean hasProfileAccess(final SecurityToken token, final User user) {

        checkNotNull( user, "Given user must not be null." );

        ObjectSet<UserProfile> profileQuery = db.query( new Predicate<UserProfile>() {

            @Override
            public boolean match(final UserProfile candidate) {

                return candidate != null && candidate.getUser().equals( user );
            }
        } );

        checkState( profileQuery.hasNext(), "User %s has no profile.", user );

        UserProfile profile = profileQuery.next();
        return securityService.hasAccess( Permission.VIEW, token, profile );
    }
}
