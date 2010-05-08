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

import static com.google.common.base.Preconditions.checkNotNull;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.user.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * <h2>{@link ACL}<br> <sub>A list of access control grants.</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class ACL {

    static final Logger logger = Logger.get( ACL.class );

    /**
     * TODO: Remove Me and use null key instead. Workaround for:
     *
     * @see <a href="http://tracker.db4o.com/browse/COR-1894">COR-1894</a>
     */
    public static User DEFAULT;

    private final Map<User, Permission> userPermissions;

    /**
     * An {@link ACL} that grants users the {@link Permission#INHERIT} permission by default.
     */
    public ACL() {

        this( Permission.INHERIT );
    }

    /**
     * @param defaultPermission The permission granted to users not explicitly specified.
     */
    public ACL(final Permission defaultPermission) {

        userPermissions = new HashMap<User, Permission>();
        setDefaultPermission( defaultPermission );
    }

    /**
     * Change the permission of a user in this access control. Any current permission of the user is revoked and replaced by the given
     * permission.
     *
     * @param permission The permission that will be granted to the given user.
     */
    public void setDefaultPermission(final Permission permission) {

        checkNotNull( permission, "Given permission must not be null." );

        userPermissions.put( DEFAULT, permission );
    }

    /**
     * Change the permission of a user in this access control. Any current permission of the user is revoked and replaced by the given
     * permission.
     *
     * @param user       The user that will be granted the given permission.
     * @param permission The permission that will be granted to the given user.
     */
    public void setUserPermission(final User user, final Permission permission) {

        checkNotNull( user, "Given user must not be null." );
        checkNotNull( permission, "Given permission must not be null." );

        userPermissions.put( user, permission );
    }

    /**
     * Unset any specific permissions for the given users in this ACL.  After this operation, the user's permissions will be determined by
     * the ACL's default permissions.
     *
     * @param user The user whose permissions to unset.
     *
     * @return The user's former permissions in this ACL or <code>null</code> if this user's permissions were already determined by the
     *         default permissions.
     */
    public Permission unsetUserPermission(final User user) {

        checkNotNull( user, "Given user must not be null." );

        return userPermissions.remove( user );
    }

    /**
     * Revoke the permission of a user in this access control.
     *
     * @param user The user that will be granted the given permission.
     */
    public void revokeUserPermission(final User user) {

        checkNotNull( user, "Given user must not be null." );

        userPermissions.remove( user );
    }

    /**
     * The user's permission is either the one set for him through {@link #setUserPermission(User, Permission)} or the default permission of
     * this ACL.
     *
     * @param user The user whose permission to look up. <code>null</code> represents an anonymous user.
     *
     * @return The permission granted to the given user by this access control.
     */
    public Permission getUserPermission(final User user) {

        if (isUserPermissionDefault( user ))
            return checkNotNull( userPermissions.get( DEFAULT ), "Default permission is unset." );

        return checkNotNull( userPermissions.get( user ), "Permission for %s is unset.", user );
    }

    /**
     * @param user The user whose permission to look up. <code>null</code> represents an anonymous user.
     *
     * @return <code>true</code> if the user's permissions in this ACL are determined by the default ACL.
     */
    public boolean isUserPermissionDefault(final User user) {

        return !userPermissions.containsKey( user );
    }

    /**
     * @return The users that have non-default permissions set in this ACL.
     */
    public Set<User> getPermittedUsers() {

        return userPermissions.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "{acl: permissions=%s}", userPermissions );
    }
}
