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

import java.io.Serializable;

import org.apache.wicket.Page;


/**
 * <h2>{@link Issue}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 3, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class Issue implements Serializable {

    private final Page location;
    private final Exception cause;


    /**
     * Create a new {@link Issue} instance.
     *
     * @param location The page that the issue occurred on.
     * @param cause    The exception that caused the issue.
     */
    public Issue(final Page location, final Exception cause) {

        this.location = location;
        this.cause = cause;
    }

    /**
     * @return The location of this {@link Issue}.
     */
    public Page getLocation() {

        return location;
    }

    /**
     * @return The cause of this {@link Issue}.
     */
    public Exception getCause() {

        return cause;
    }
}
