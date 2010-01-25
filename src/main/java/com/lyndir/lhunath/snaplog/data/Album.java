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
package com.lyndir.lhunath.snaplog.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.LinkedList;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;


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
public abstract class Album implements Serializable {

    private User user;
    private String name;
    private LinkedList<Media> media;


    protected Album(User user, String name) {

        setUser( user );
        setName( name );
    }

    /**
     * @return The user of this {@link Album}.
     */
    public User getUser() {

        return user;
    }

    /**
     * @param user The user of this {@link Album}.
     */
    public void setUser(User user) {

        this.user = checkNotNull( user );
    }

    /**
     * @return The name of this {@link Album}.
     */
    public String getName() {

        return name;
    }

    /**
     * @param name The name of this {@link Album}.
     */
    public void setName(String name) {

        this.name = checkNotNull( name );
    }

    /**
     * @return The media of this {@link Album}.
     */
    public ImmutableList<Media> getMedia() {

        return ImmutableList.copyOf( media );
    }

    /**
     * @param media The media of this {@link Album}.
     */
    public void setMedia(LinkedList<Media> media) {

        this.media = checkNotNull( media );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;

        if (o instanceof Album)
            return Objects.equal( ((Album) o).getUser(), getUser() )
               && Objects.equal( ((Album) o).getName(), getName() );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hashCode( getUser(), getName() );
    }
}
