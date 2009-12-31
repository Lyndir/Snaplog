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
package com.lyndir.lhunath.snaplog.model;

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
public class User {

    private String name;
    private char   badge = '~';


    /**
     * Create a new {@link User} instance.
     * 
     * @param name
     *            The user's username.
     */
    public User(String name) {

        setName( name );
    }

    /**
     * @return The name of this {@link User}.
     */
    public String getName() {

        return name;
    }

    /**
     * @param name
     *            The name of this {@link User}.
     */
    public void setName(String name) {

        this.name = name;
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
}
