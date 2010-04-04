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
import com.lyndir.lhunath.snaplog.data.user.UserProfile;


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
public abstract class Album extends AbstractSecureObject<UserProfile> implements Serializable {

    private UserProfile ownerProfile;
    private String name;
    private String description;


    /**
     * @param ownerProfile The profile of the user that owns this album.
     * @param name         A unique, ownerProfile-visible name of this album amongst the ownerProfile's albums.
     */
    protected Album(UserProfile ownerProfile, String name) {

        setOwnerProfile( ownerProfile );
        setName( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getParent() {

        return getOwnerProfile();
    }

    /**
     * @return The profile of the user that owns this album.
     */
    public UserProfile getOwnerProfile() {

        return checkNotNull( ownerProfile, "Given ownerProfile must not be null." );
    }

    /**
     * @param ownerProfile The profile of the user that owns this album.
     */
    public void setOwnerProfile(UserProfile ownerProfile) {

        this.ownerProfile = checkNotNull( ownerProfile, "Given ownerProfile must not be null." );
    }

    /**
     * @return The name of this {@link Album}.
     */
    public String getName() {

        return checkNotNull( name, "Name must not be null." );
    }

    /**
     * @param name The name of this {@link Album}.
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
     * @param description The description of this {@link Album}.
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
            return Objects.equal( ((Album) o).getOwnerProfile(), getOwnerProfile() )
                   && Objects.equal( ((Album) o).getName(), getName() );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hashCode( getOwnerProfile(), getName() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "{album: name=%s, ownerProfile=%s}", name, ownerProfile );
    }
}
