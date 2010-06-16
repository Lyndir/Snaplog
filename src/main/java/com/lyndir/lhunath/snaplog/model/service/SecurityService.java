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
package com.lyndir.lhunath.snaplog.model.service;

import com.lyndir.lhunath.lib.system.collection.Pair;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecureObject;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.IllegalOperationException;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * <h2>{@link SecurityService}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SecurityService {

    /**
     * @param permission The permission required on the given object to proceed with the request.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param o          The object that is the target of the request.
     *
     * @throws PermissionDeniedException When permission is required and the object is not <code>null</code> while there is no security
     *                                   token or the token doesn't grant the necessary permission on the object.
     * @see #hasAccess(Permission, SecurityToken, SecureObject)
     */
    void assertAccess(Permission permission, SecurityToken token, SecureObject<?> o)
            throws PermissionDeniedException;

    /**
     * Check whether the given token grants permission to perform an operation on the given object that requires the given permission.
     *
     * @param permission The permission required on the given object to proceed with the request.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param o          The object that is the target of the request.
     *
     * @return <code>true</code>: The given token grants the given permission on the given object.
     */
    boolean hasAccess(Permission permission, SecurityToken token, SecureObject<?> o);

    /**
     * Filter an iterator of SecureObjects, only allowing those on which the given token provides the given permission.
     *
     * @param permission The permission required on objects from the given iterator.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param source     The source of objects to filter.
     *
     * @return An iterator that provides elements from the source on which the given permission is granted for the given token.
     */
    <T extends SecureObject<?>> Iterator<T> filterAccess(Permission permission, SecurityToken token, Iterator<T> source);

    /**
     * Filter an iterator of SecureObjects, only allowing those on which the given token provides the given permission.
     *
     * @param permission The permission required on objects from the given iterator.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param source     The source of objects to filter.
     *
     * @return An iterator that provides elements from the source on which the given permission is granted for the given token.
     */
    <T extends SecureObject<?>> ListIterator<T> filterAccess(Permission permission, SecurityToken token, ListIterator<T> source);

    /**
     * @param token The token used to authenticate the available permissions on the given object.
     * @param o     The object whose permissions to retrieve.
     *
     * @return The permissions granted to any user that doesn't have a specific user permission set on the object.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    Permission getDefaultPermission(final SecurityToken token, final SecureObject<?> o)
            throws PermissionDeniedException;

    /**
     * @param token The token used to authenticate the available permissions on the given object.
     * @param user  The user who's permission on the given object to retrieve.  <code>null</code> represents an anonymous user.
     * @param o     The object whose permissions to retrieve.
     *
     * @return The effective permissions granted for this object to the given user are this object's permissions for the user or its
     *         parent's effective permissions for the user if this object's permissions for the user are Permission#INHERIT.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    Permission getEffectivePermissions(SecurityToken token, User user, SecureObject<?> o)
            throws PermissionDeniedException;

    /**
     * @param token The token used to authenticate the available permissions on the given object.
     * @param o     The object whose permissions to retrieve.
     *
     * @return All users that have non-default access to the given object and the actual permissions granted to them for the object.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    Iterator<Pair<User, Permission>> iterateUserPermissions(SecurityToken token, SecureObject<?> o)
            throws PermissionDeniedException;

    /**
     * @param token The token used to authenticate the available permissions on the given object.
     * @param o     The object whose permissions to retrieve.
     *
     * @return The total number of users that have non-default access to the given object.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    int countPermittedUsers(SecurityToken token, SecureObject<?> o)
            throws PermissionDeniedException;

    /**
     * Change the permissions a given to any users without specific permissions.
     *
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param o          The object whose permissions must be modified.
     * @param permission The new permissions the to set on the given secure object for the given user.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    void setDefaultPermission(SecurityToken token, SecureObject<?> o, Permission permission)
            throws PermissionDeniedException;

    /**
     * Change the permissions a given user has on a given secure object.
     *
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param o          The object whose permissions must be modified.
     * @param user       The user for whom permissions must be modified.
     * @param permission The new permissions the to set on the given secure object for the given user.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     * @throws IllegalOperationException When the given user is the object's owner.
     */
    void setUserPermission(SecurityToken token, SecureObject<?> o, User user, Permission permission)
            throws PermissionDeniedException, IllegalOperationException;
}
