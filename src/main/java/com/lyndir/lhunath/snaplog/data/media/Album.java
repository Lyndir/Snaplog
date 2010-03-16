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
package com.lyndir.lhunath.snaplog.data.media;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.lyndir.lhunath.snaplog.data.security.AbstractSecureObject;
import com.lyndir.lhunath.snaplog.data.user.User;


/**
 * <h2>{@link Album}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class Album extends AbstractSecureObject<User> implements Serializable {

    private User ownerUser;
    private String name;
    private String description;


    /**
     * @param ownerUser
     *            The ownerUser that owns this Album.
     * @param name
     *            A unique, ownerUser-visible name of this Album amongst the ownerUser's albums.
     */
    protected Album(User ownerUser, String name) {

        setOwnerUser( ownerUser );
        setName( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getParent() {

        return getOwnerUser();
    }

    /**
     * @return The ownerUser of this {@link Album}.
     */
    public User getOwnerUser() {

        return checkNotNull( ownerUser, "Given ownerUser must not be null." );
    }

    /**
     * @param ownerUser
     *            The ownerUser of this {@link Album}.
     */
    public void setOwnerUser(User ownerUser) {

        this.ownerUser = checkNotNull( ownerUser, "Given ownerUser must not be null." );
    }

    /**
     * @return The name of this {@link Album}.
     */
    public String getName() {

        return checkNotNull( name, "Name must not be null." );
    }

    /**
     * @param name
     *            The name of this {@link Album}.
     */
    public void setName(String name) {

        this.name = checkNotNull( name, "Given album name must not be null." );
    }

    /**
     * @return The description of this {@link Album}.
     */
    public String getDescription() {

        return description;
    }

    /**
     * @param description
     *            The description of this {@link Album}.
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;

        if (o instanceof Album)
            return Objects.equal( ((Album) o).getOwnerUser(), getOwnerUser() )
                   && Objects.equal( ((Album) o).getName(), getName() );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hashCode( getOwnerUser(), getName() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "{album: name=%s, owner=%s}", name, ownerUser );
    }
}
