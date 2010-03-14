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

import com.lyndir.lhunath.snaplog.data.user.User;


/**
 * <h2>{@link SecurityToken}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 14, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class SecurityToken {

    /**
     * Use this token <b>ONLY</b> for requests that the user can't gain anything from. The result must not be given or
     * hinted at to the user.
     */
    public static final SecurityToken INTERNAL_USE_ONLY = new SecurityToken( null ) {

        @Override
        public boolean isInternalUseOnly() {

            return true;
        }
    };

    private User actor;


    /**
     * @param actor
     *            The user that has requested or will gain access to the result of the operation.
     */
    public SecurityToken(User actor) {

        this.actor = actor;
    }

    /**
     * @return The user that has requested or will gain access to the result of the operation.
     */
    public User getActor() {

        return actor;
    }

    /**
     * @return <code>true</code>: This token should allow operations regardless. It can only be <code>true</code> for
     *         requests made using the {@link #INTERNAL_USE_ONLY} token.
     */
    public boolean isInternalUseOnly() {

        return false;
    }
}
