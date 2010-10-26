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
package com.lyndir.lhunath.snaplog.data.object.media;

import static com.google.common.base.Preconditions.checkNotNull;

import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.security.AbstractSecureObject;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;


/**
 * <h2>{@link Source}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class Source extends AbstractSecureObject<UserProfile> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final UserProfile ownerProfile;

    /**
     * @param ownerProfile The profile of the user that owns this source.
     */
    protected Source(final UserProfile ownerProfile) {

        super( null );

        this.ownerProfile = ownerProfile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getParent() {

        return getOwnerProfile();
    }

    /**
     * @return The profile of the user that owns this source.
     */
    public UserProfile getOwnerProfile() {

        return checkNotNull( ownerProfile, "Given ownerProfile must not be null." );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "{source: owner=%s}", ownerProfile );
    }

    @Override
    public String typeDescription() {

        return msgs.type();
    }

    interface Messages {

        /**
         * @return The name of this type.
         */
        String type();
    }
}
