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
package com.lyndir.lhunath.snaplog.webapp.cookie;

import com.lyndir.lhunath.snaplog.data.object.user.User;


/**
 * <h2>{@link LastUserCookieManager}<br>
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
public abstract class LastUserCookieManager {

    /**
     * @return The user that was previosly used by the browser or <code>null</code> if unknown.
     */
    public static User findLastUser() {

        // TODO: Load the last user from the last-user cookie.

        return null;
    }

    /**
     * Remember the current user as the one to return in subsequent calls to {@link #findLastUser}
     */
    public static void rememberCurrentUser() {

        // TODO: Write out the last-user cookie.
    }
}
