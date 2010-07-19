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
package com.lyndir.lhunath.snaplog.model.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.SizedListIterator;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.UserDAO;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.error.UsernameTakenException;
import com.lyndir.lhunath.snaplog.model.ServiceModule;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.model.service.UserService;


/**
 * <h2>{@link UserServiceImpl}<br>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public class UserServiceImpl implements UserService {

    final SecurityService securityService;
    private final UserDAO userDAO;

    /**
     * @param userDAO         See {@link ServiceModule}.
     * @param securityService See {@link ServiceModule}.
     */
    @Inject
    public UserServiceImpl(final UserDAO userDAO, final SecurityService securityService) {

        this.userDAO = userDAO;
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

        UserProfile userProfile = new UserProfile( new User( linkID, userName ) );
        userDAO.update( userProfile );

        return userProfile.getUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserWithLinkID(final LinkID linkID) {

        return userDAO.findUser( linkID );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserWithUserName(final String userName) {

        checkNotNull( userName, "Given userName must not be null." );

        SizedListIterator<User> userQuery = iterateUsers( new IPredicate<User>() {

            @Override
            public boolean apply(final User input) {

                return input != null && ObjectUtils.equal( input.getUserName(), userName );
            }
        } );
        if (userQuery.hasNext())
            return userQuery.next();

        // No user exists yet for given userName.
        return null;
    }

    @Override
    public User getUserWithUserName(final String userName)
            throws UserNotFoundException {

        User user = findUserWithUserName( userName );
        if (user == null)
            throw new UserNotFoundException( userName );

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SizedListIterator<User> iterateUsers(final Predicate<User> predicate) {

        if (predicate == null)
            return SizedListIterator.of( userDAO.listUsers() );

        return SizedListIterator.of( userDAO.listUsers( predicate ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getProfile(final SecurityToken token, final User user)
            throws PermissionDeniedException {

        UserProfile userProfile = userDAO.findUserProfile( user );
        checkNotNull( userProfile, "User %s has no profile.", user );

        securityService.assertAccess( Permission.VIEW, token, userProfile );
        return userProfile;
    }

    @Override
    public boolean hasProfileAccess(final SecurityToken token, final User user) {

        try {
            getProfile( token, user );

            return true;
        }

        catch (PermissionDeniedException ignored) {
            return false;
        }
    }
}
