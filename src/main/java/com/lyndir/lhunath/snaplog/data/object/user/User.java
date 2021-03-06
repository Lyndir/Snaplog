/*
 *   Copyright 2009, Maarten Billemont
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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.lyndir.lhunath.opal.security.Subject;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;


/**
 * <h2>{@link User}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Dec 31, 2009</i> </p>
 *
 * @author lhunath
 */
public class User implements Subject {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private LinkID linkID;

    private String userName;
    private char badge = '~';

    /**
     * Create a new {@link User} instance.
     *
     * @param linkID   The user's {@link LinkID}.
     * @param userName The user's userName.
     */
    public User(final LinkID linkID, final String userName) {

        setLinkID( linkID );
        setUserName( userName );
    }

    /**
     * @return The linkID of this {@link User}.
     */
    public LinkID getLinkID() {

        return linkID;
    }

    /**
     * @param linkID The linkID of this {@link User}.
     */
    public void setLinkID(final LinkID linkID) {

        this.linkID = checkNotNull( linkID, "Given linkID must not be null." );
    }

    /**
     * @return The userName of this {@link User}.
     */
    public String getUserName() {

        return userName;
    }

    /**
     * @param userName The userName of this {@link User}.
     */
    public void setUserName(final String userName) {

        this.userName = checkNotNull( userName, "Given userName must not be null." );
    }

    /**
     * @return The badge of this {@link User}.
     */
    public char getBadge() {

        return badge;
    }

    /**
     * @param badge The badge of this {@link User}.
     */
    public void setBadge(final char badge) {

        this.badge = badge;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {

        if (o == this)
            return true;
        if (o instanceof User)
            return Objects.equal( ((User) o).getLinkID(), getLinkID() );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hashCode( getLinkID() );
    }

    @Override
    public String getLocalizedType() {

        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {

        return msgs.instance( getBadge(), getUserName() );
    }

    interface Messages {
        String type();

        String instance(char badge, String userName);
    }
}
