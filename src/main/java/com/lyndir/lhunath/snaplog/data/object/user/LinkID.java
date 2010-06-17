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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import java.io.Serializable;


/**
 * <h2>{@link LinkID}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public class LinkID implements Serializable {

    private String applicationScopedUserIdentifier;

    /**
     * Create a new {@link LinkID} instance.
     *
     * @param applicationScopedUserIdentifier
     *         The user identifier that linkID uses to identify a certain user to this application.
     */
    public LinkID(final String applicationScopedUserIdentifier) {

        setApplicationScopedUserIdentifier( applicationScopedUserIdentifier );
    }

    /**
     * @return The applicationScopedUserIdentifier of this {@link LinkID}.
     */
    public String getApplicationScopedUserIdentifier() {

        return applicationScopedUserIdentifier;
    }

    /**
     * @param applicationScopedUserIdentifier
     *         The applicationScopedUserIdentifier of this {@link LinkID}.
     */
    public void setApplicationScopedUserIdentifier(final String applicationScopedUserIdentifier) {

        this.applicationScopedUserIdentifier = checkNotNull( applicationScopedUserIdentifier,
                                                             "Given applicationScopedUserIdentifier must not be null." );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "{linkid:%s}", getApplicationScopedUserIdentifier() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {

        if (o == this)
            return true;
        if (o instanceof LinkID)
            return Objects.equal( ((LinkID) o).getApplicationScopedUserIdentifier(), getApplicationScopedUserIdentifier() );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hashCode( getApplicationScopedUserIdentifier() );
    }
}
