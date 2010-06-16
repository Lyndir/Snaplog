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
package com.lyndir.lhunath.snaplog.webapp;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebSession;


/**
 * <h2>{@link SnaplogSession}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Dec 31, 2009</i> </p>
 *
 * @author lhunath
 */
public class SnaplogSession extends WebSession {

    private static final Logger logger = Logger.get( SnaplogSession.class );

    private User activeUser;
    private User focusedUser;
    private Album focusedAlbum;

    /**
     * @param request The {@link Request} that started the session.
     */
    public SnaplogSession(final Request request) {

        super( request );
    }

    /**
     * @return {@link Session#get()}
     */
    public static SnaplogSession get() {

        return (SnaplogSession) Session.get();
    }

    /**
     * @return The activeUser of this {@link SnaplogSession}.
     */
    public User getActiveUser() {

        return activeUser;
    }

    /**
     * @param activeUser The activeUser of this {@link SnaplogSession}.
     */
    public void setActiveUser(final User activeUser) {

        if (Objects.equal( getActiveUser(), activeUser ))
            return;

        logger.inf( "Session user identification changed from: %s, to: %s", //
                    getActiveUser(), activeUser );

        this.activeUser = activeUser;
    }

    /**
     * @return <code>true</code>: An active user is set ({@link #getActiveUser()} != <code>null</code>).
     */
    public boolean isAuthenticated() {

        return getActiveUser() != null;
    }

    /**
     * @return The focusedUser of this {@link SnaplogSession}.
     */
    public User getFocusedUser() {

        if (focusedAlbum != null)
            // These SHOULD always match if an album is focused.
            checkState( ObjectUtils.equal( focusedAlbum.getOwnerProfile().getUser(), focusedUser ) );
        if (focusedUser == null)
            // Focus on the active user if not focusing on anyone.
            setFocusedUser( activeUser );

        return focusedUser;
    }

    /**
     * @param focusedUser The focusedUser of this {@link SnaplogSession}.
     */
    public void setFocusedUser(final User focusedUser) {

        if (focusedAlbum != null && !ObjectUtils.equal( focusedAlbum.getOwnerProfile().getUser(), focusedUser ))
            // User is no longer the focused album owner; unfocus the album.
            setFocusedAlbum( null );

        this.focusedUser = focusedUser;
    }

    /**
     * @return The focusedAlbum of this {@link SnaplogSession}.
     */
    public Album getFocusedAlbum() {

        if (focusedAlbum != null)
            // These SHOULD always match if an album is focused.
            checkState( ObjectUtils.equal( focusedAlbum.getOwnerProfile().getUser(), focusedUser ) );

        return focusedAlbum;
    }

    /**
     * @param focusedAlbum The focusedAlbum of this {@link SnaplogSession}.
     */
    public void setFocusedAlbum(final Album focusedAlbum) {

        if (focusedAlbum != null)
            // Focusing a specific album; set focused user to the album owner.
            setFocusedUser( focusedAlbum.getOwnerProfile().getUser() );

        this.focusedAlbum = focusedAlbum;
    }

    /**
     * @return A new {@link SecurityToken} that represents the context of the currently active user.
     */
    public SecurityToken newToken() {

        return new SecurityToken( getActiveUser() );
    }

    public static IModel<Album> getFocusedAlbumProxyModel() {

        return new IModel<Album>() {

            @Override
            public void detach() {

            }

            @Override
            public Album getObject() {

                return get().getFocusedAlbum();
            }

            @Override
            public void setObject(final Album object) {

                get().setFocusedAlbum( object );
            }
        };
    }
}
