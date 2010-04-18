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
package com.lyndir.lhunath.snaplog.model.impl;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.media.AlbumData;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecureObject;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.model.SecurityService;


/**
 * <h2>{@link SecurityServiceImpl}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 14, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class SecurityServiceImpl implements SecurityService {

    static final Logger logger = Logger.get( SecurityServiceImpl.class );


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAccess(final Permission permission, final SecurityToken token, final SecureObject<?> o) {

        try {
            assertAccess( permission, token, o );
            return true;
        }

        catch (PermissionDeniedException ignored) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assertAccess(final Permission permission, final SecurityToken token, final SecureObject<?> o)
            throws PermissionDeniedException {

        if (o == null || permission == Permission.NONE) {
            // No permission required.
            logger.dbg( "Permisson Granted: No permission necessary for: %s@%s", //
                        permission, o );
            return;
        }

        if (token == null) {
            // Permission required but no token given.
            logger.dbg( "Permission Denied: Missing security token for: %s@%s", //
                        permission, o );
            throw new PermissionDeniedException( String.format( "No security token in request for %s@%s.", //
                                                                permission, o ) );
        }

        if (token.isInternalUseOnly()) {
            // Token is "Internal Use", grant everything.
            logger.dbg( "Permisson Granted: INTERNAL_USE token for: %s@%s", //
                        permission, o );
            return;
        }

        Permission tokenPermission = o.getACL().getUserPermission( token.getActor() );
        if (tokenPermission == Permission.INHERIT) {
            if (o.getParent() == null) {
                logger.dbg( "Permission Denied: Can't inherit permissions, no parent set for: %s@%s", //
                            permission, o );
                throw new PermissionDeniedException(
                        String.format( "Had to inherit permission for %s@%s but no parent set.", //
                                       permission, o ) );
            }

            logger.dbg( "Inheriting permission for: %s@%s", //
                        permission, o );
            assertAccess( permission, token, o.getParent() );
            return;
        }

        if (!isPermissionProvided( tokenPermission, permission )) {
            logger.dbg( "Permission Denied: Token authorizes %s, insufficient for: %s@%s", //
                        tokenPermission, permission, o );
            throw new PermissionDeniedException(
                    String.format( "Security Token %s grants permissions %s but request required %s on object %s", //
                                   token, tokenPermission, permission, o ) );
        }

        logger.dbg( "Permission Granted: Token authorization %s matches for: %s@%s", //
                    tokenPermission, permission, o );
    }

    private static boolean isPermissionProvided(final Permission givenPermission,
                                                final Permission requestedPermission) {

        if (givenPermission == requestedPermission)
            return true;
        if (givenPermission == null || requestedPermission == null)
            return false;

        for (final Permission inheritedGivenPermission : givenPermission.getProvided())
            if (isPermissionProvided( inheritedGivenPermission, requestedPermission ))
                return true;

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Media> iterateFilesFor(final SecurityToken token, final AlbumData albumData) {

        return Iterators.filter( albumData.getInternalFiles( this ).iterator(), new Predicate<Media>() {

            @Override
            public boolean apply(final Media input) {

                return hasAccess( Permission.VIEW, token, input );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<MediaTimeFrame> iterateTimeFramesFor(final SecurityToken token, final AlbumData albumData) {

        return Iterators.filter( albumData.getInternalTimeFrames( this ).iterator(), new Predicate<MediaTimeFrame>() {

            @Override
            public boolean apply(final MediaTimeFrame input) {

                // TODO: Implement security on MediaTimeFrames.
                return true;// hasAccess( Permission.VIEW, token, input );
            }
        } );
    }
}
