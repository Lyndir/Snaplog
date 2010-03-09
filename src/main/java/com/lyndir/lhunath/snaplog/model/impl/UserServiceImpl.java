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
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.LinkID;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.model.UserService;


/**
 * <h2>{@link UserServiceImpl}<br>
 * 
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that we can service.
 * @author lhunath
 */
public class UserServiceImpl<P extends Provider> implements UserService<P> {

    ObjectContainer db;


    @Inject
    public UserServiceImpl(ObjectContainer db) {

        this.db = db;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(LinkID linkID, String userName) {

        checkNotNull( linkID );
        checkNotNull( userName );

        User user = new User( linkID, userName );
        db.store( user );

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserWithLinkID(final LinkID linkID) {

        checkNotNull( linkID );

        ObjectSet<User> userQuery = db.query( new Predicate<User>() {

            @Override
            public boolean match(User candidate) {

                return candidate.getLinkID().equals( linkID );
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

        checkNotNull( userName );

        ObjectSet<User> userQuery = db.query( new Predicate<User>() {

            @Override
            public boolean match(User candidate) {

                return candidate.getUserName().equals( userName );
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
    public ObjectSet<Album<P>> queryAlbumsOfUserVisibleToUser(final User ownerUser, User observerUser) {

        checkNotNull( ownerUser );

        return db.query( new Predicate<Album<P>>() {

            @Override
            public boolean match(Album<P> candidate) {

                return candidate.getUser().equals( ownerUser );
            }
        } );
    }
}
