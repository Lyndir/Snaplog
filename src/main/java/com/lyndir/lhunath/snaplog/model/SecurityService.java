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
package com.lyndir.lhunath.snaplog.model;

import java.util.Iterator;

import com.lyndir.lhunath.snaplog.data.media.AlbumData;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecureObject;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;


/**
 * <h2>{@link SecurityService}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 14, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public interface SecurityService {

    /**
     * @param permission
     *            The permission required on the given object to proceed with the request.
     * @param token
     *            The token used to authenticate the available permissions on the given object.
     * @param o
     *            The object that is the target of the request.
     * 
     * @throws PermissionDeniedException
     *             When permission is required and the object is not <code>null</code> while there is no security token
     *             or the token doesn't grant the necessary permission on the object.
     * 
     * @see #hasAccess(Permission, SecurityToken, SecureObject)
     */
    public void assertAccess(Permission permission, SecurityToken token, SecureObject<?> o)
            throws PermissionDeniedException;

    /**
     * Check whether the given token grants permission to perform an operation on the given object that requires the
     * given permission.
     * 
     * @param permission
     *            The permission required on the given object to proceed with the request.
     * @param token
     *            The token used to authenticate the available permissions on the given object.
     * @param o
     *            The object that is the target of the request.
     * @return <code>true</code>: The given token grants the given permission on the given object.
     */
    public boolean hasAccess(Permission permission, SecurityToken token, SecureObject<?> o);

    /**
     * @param token
     *            Request authentication token should authorize {@link Permission#VIEW} on the album data's media to
     *            return.
     * @param albumData
     *            The data to retrieve files from.
     * @return All files from the given albumData that the given token authorizes access to.
     */
    public Iterator<Media> iterateFilesFor(SecurityToken token, AlbumData albumData);

    /**
     * @param token
     *            Request authentication token should authorize {@link Permission#VIEW} on the album data's media to
     *            return. (TODO: Not implemented)
     * @param albumData
     *            The data to retrieve timeFrames from.
     * @return All timeFrames from the given albumData that the given token authorizes access to.
     */
    public Iterator<MediaTimeFrame> iterateTimeFramesFor(SecurityToken token, AlbumData albumData);
}
