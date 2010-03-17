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

/**
 * <h2>{@link SecureObject}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 14, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of the parent object.
 * @author lhunath
 */
public interface SecureObject<P extends SecureObject<?>> {

    /**
     * @return The {@link SecureObject} that we inherit metadata from.
     */
    P getParent();

    /**
     * @return The access control set governing the permissions users have over this object.
     */
    ACL getACL();
}