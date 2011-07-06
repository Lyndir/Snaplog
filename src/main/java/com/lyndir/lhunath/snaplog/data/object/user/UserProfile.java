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
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.security.SGlobalSecureObject;
import com.lyndir.lhunath.snaplog.security.SSecureObject;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * <h2>{@link UserProfile}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 24, 2010</i> </p>
 *
 * @author lhunath
 */
public class UserProfile extends SSecureObject<SGlobalSecureObject> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    /**
     * Create a new {@link UserProfile} instance.
     *
     * @param user The user that this profile describes.
     */
    public UserProfile(final User user) {

        super( user );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SGlobalSecureObject getParent() {

        return SGlobalSecureObject.DEFAULT;
    }

    /**
     * @return The user of this {@link UserProfile}.
     */
    public User getUser() {

        return (User) getOwner();
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o)
            return true;
        if (!(o instanceof UserProfile))
            return false;

        return Objects.equal( getUser(), ((UserProfile) o).getUser() );
    }

    @Override
    public int hashCode() {

        return Objects.hashCode( getUser() );
    }

    @Override
    public String toString() {

        return String.format( "{profile: user=%s}", getUser() );
    }

    @Override
    public String getLocalizedType() {

        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {

        return msgs.instance( getUser().getUserName() );
    }

    private void readObject(final ObjectInputStream stream)
            throws IOException, ClassNotFoundException {

        // Default deserialization.
        stream.defaultReadObject();

        // Manually load a new Messages proxy.
        MessagesFactory.initialize( this, Messages.class );
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
        String instance(String userName);
    }
}
