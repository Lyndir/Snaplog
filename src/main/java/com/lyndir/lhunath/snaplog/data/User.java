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
package com.lyndir.lhunath.snaplog.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.base.Objects;


/**
 * <h2>{@link User}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Dec 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class User implements Serializable {

    private LinkID linkID;

    private String userName;
    private char   badge = '~';


    /**
     * Create a new {@link User} instance.
     * 
     * @param linkID
     *            The user's {@link LinkID}.
     * @param userName
     *            The user's userName.
     */
    public User(LinkID linkID, String userName) {

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
     * @param linkID
     *            The linkID of this {@link User}.
     */
    public void setLinkID(LinkID linkID) {

        this.linkID = checkNotNull( linkID );
    }

    /**
     * @return The userName of this {@link User}.
     */
    public String getUserName() {

        return userName;
    }

    /**
     * @param userName
     *            The userName of this {@link User}.
     */
    public void setUserName(String userName) {

        this.userName = checkNotNull( userName );
    }

    /**
     * @return The badge of this {@link User}.
     */
    public char getBadge() {

        return badge;
    }

    /**
     * @param badge
     *            The badge of this {@link User}.
     */
    public void setBadge(char badge) {

        this.badge = badge;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "%s%s", getBadge(), getUserName() );
    }
}
