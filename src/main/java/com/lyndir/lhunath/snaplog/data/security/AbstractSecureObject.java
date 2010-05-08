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
package com.lyndir.lhunath.snaplog.data.security;

import com.lyndir.lhunath.lib.system.logging.exception.InternalInconsistencyException;
import com.lyndir.lhunath.snaplog.data.user.User;


/**
 * <h2>{@link AbstractSecureObject}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 * @param <P> The type of the parent object.
 */
public abstract class AbstractSecureObject<P extends SecureObject<?>> implements SecureObject<P> {

    private User owner;
    private final ACL acl = new ACL();

    @Override
    public User getOwner() {

        if (owner == null) {
            if (getParent() != null)
                return getParent().getOwner();

            throw new InternalInconsistencyException( "Cannot determine the owner since it is not set and the object has no parent." );
        }

        return owner;
    }

    /**
     * @param owner The new owner of this object.
     */
    public void setOwner(final User owner) {
        this.owner = owner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ACL getACL() {

        return acl;
    }
}
