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
package com.lyndir.lhunath.snaplog.data.user;

import java.io.Serializable;

import com.lyndir.lhunath.snaplog.data.security.AbstractSecureObject;
import com.lyndir.lhunath.snaplog.data.security.GlobalSecureObject;
import com.lyndir.lhunath.snaplog.data.security.Permission;


/**
 * <h2>{@link UserProfile}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 24, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class UserProfile extends AbstractSecureObject<GlobalSecureObject> implements Serializable {

    private User user;


    /**
     * Create a new {@link UserProfile} instance.
     *
     * @param user The user that this profile describes.
     */
    public UserProfile(User user) {

        this.user = user;

        // User automatically gets CONTRIBUTE permissions on himself.
        getACL().setUserPermission( user, Permission.CONTRIBUTE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GlobalSecureObject getParent() {

        return GlobalSecureObject.DEFAULT;
    }

    /**
     * @return The user of this {@link UserProfile}.
     */
    public User getUser() {

        return user;
    }

    /**
     * @param user The user of this {@link UserProfile}.
     */
    public void setUser(User user) {

        this.user = user;
    }
}
