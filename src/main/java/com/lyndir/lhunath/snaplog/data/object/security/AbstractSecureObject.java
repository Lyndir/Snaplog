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
package com.lyndir.lhunath.snaplog.data.object.security;

import com.lyndir.lhunath.snaplog.data.object.user.User;
import net.link.safeonline.util.validation.annotation.NotNull;


/**
 * <h2>{@link AbstractSecureObject}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @param <P> The type of the parent object.
 *
 * @author lhunath
 */
public abstract class AbstractSecureObject<P extends SecureObject<?>> implements SecureObject<P> {

    private final User owner;
    private final ACL acl = new ACL();

    protected AbstractSecureObject() {

        owner = null;
    }

    protected AbstractSecureObject(@NotNull final User owner) {

        this.owner = owner;
    }

    @Override
    public User getOwner() {

        if (owner == null)
            if (getParent() != null)
                return getParent().getOwner();

        return owner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ACL getACL() {

        return acl;
    }
}
