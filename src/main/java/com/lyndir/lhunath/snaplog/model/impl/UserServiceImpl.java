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

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.LinkID;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;


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

    private static final Map<LinkID, User> users = new HashMap<LinkID, User>();


    @Inject
    public UserServiceImpl() {

        users.put( SnaplogConstants.DEFAULT_USER.getLinkID(), SnaplogConstants.DEFAULT_USER );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(LinkID linkID, String userName) {

        checkNotNull( linkID );
        checkNotNull( userName );

        return users.put( linkID, new User( linkID, userName ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserWithLinkID(LinkID linkID) {

        checkNotNull( linkID );

        return users.get( linkID );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserWithName(String userName) {

        checkNotNull( userName );

        for (User user : users.values())
            if (user.getUserName().equals( userName ))
                return user;

        return null;
    }
}
