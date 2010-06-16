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
package com.lyndir.lhunath.snaplog.data.object.user;

import com.google.common.base.Objects;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.security.AbstractSecureObject;
import com.lyndir.lhunath.snaplog.data.object.security.GlobalSecureObject;
import java.io.Serializable;


/**
 * <h2>{@link UserProfile}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 24, 2010</i> </p>
 *
 * @author lhunath
 */
public class UserProfile extends AbstractSecureObject<GlobalSecureObject> implements Serializable {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private User user;

    /**
     * Create a new {@link UserProfile} instance.
     *
     * @param user The user that this profile describes.
     */
    public UserProfile(final User user) {

        setUser( user );
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
    public void setUser(final User user) {

        setOwner( this.user = user );
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o)
            return true;
        if (!(o instanceof UserProfile))
            return false;

        return Objects.equal( user, ((UserProfile) o).user );
    }

    @Override
    public int hashCode() {

        return Objects.hashCode( user );
    }

    @Override
    public String toString() {

        return String.format( "{profile: user=%s}", user );
    }

    @Override
    public String typeDescription() {

        return msgs.type();
    }

    @Override
    public String objectDescription() {

        return msgs.description( user.getUserName() );
    }

    interface Messages {

        /**
         * @return The name of this type.
         */
        String type();

        /**
         * @param userName The userName of the profile's user.
         *
         * @return A description of this profile.
         */
        String description(String userName);
    }
}
