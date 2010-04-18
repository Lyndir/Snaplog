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
import com.lyndir.lhunath.lib.system.util.SafeObjects;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebSession;


/**
 * <h2>{@link SnaplogSession}<br>
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
public class SnaplogSession extends WebSession {

    private static final Logger logger = Logger.get( SnaplogSession.class );

    private Panel activeContent;
    private Tab activeTab;
    private User activeUser;
    private User focussedUser;
    private Album focussedAlbum;


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
     * @return The activeContent of this {@link SnaplogSession}.
     */
    public Panel getActiveContent() {

        return activeContent;
    }

    /**
     * @param activeContent The activeContent of this {@link SnaplogSession}.
     */
    public void setActiveContent(final Panel activeContent) {

        this.activeContent = activeContent;
    }

    /**
     * @return The activeTab of this {@link SnaplogSession}.
     */
    public Tab getActiveTab() {

        return activeTab;
    }

    /**
     * @param activeTab The activeTab of this {@link SnaplogSession}.
     */
    public void setActiveTab(final Tab activeTab) {

        checkState( activeTab.get().isVisible(), "Cannot set the invisible tab %s as active.", activeTab );

        this.activeTab = activeTab;
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
     * @return The focussedUser of this {@link SnaplogSession}.
     */
    public User getFocussedUser() {

        if (focussedAlbum != null)
            // These SHOULD always match if an album is focussed.
            checkState( SafeObjects.equal( focussedAlbum.getOwnerProfile().getUser(), focussedUser ) );
        if (focussedUser == null)
            // Focus on the active user if not focusing on anyone.
            setFocussedUser( activeUser );

        return focussedUser;
    }

    /**
     * @param focussedUser The focussedUser of this {@link SnaplogSession}.
     */
    public void setFocussedUser(final User focussedUser) {

        if (focussedAlbum != null && !SafeObjects.equal( focussedAlbum.getOwnerProfile().getUser(), focussedUser ))
            // User is no longer the focussed album owner; unfocus the album.
            setFocussedAlbum( null );

        this.focussedUser = focussedUser;
    }

    /**
     * @return The focussedAlbum of this {@link SnaplogSession}.
     */
    public Album getFocussedAlbum() {

        if (focussedAlbum != null)
            // These SHOULD always match if an album is focussed.
            checkState( SafeObjects.equal( focussedAlbum.getOwnerProfile().getUser(), focussedUser ) );

        return focussedAlbum;
    }

    /**
     * @param focussedAlbum The focussedAlbum of this {@link SnaplogSession}.
     */
    public void setFocussedAlbum(final Album focussedAlbum) {

        if (focussedAlbum != null)
            // Focusing a specific album; set focussed user to the album owner.
            setFocussedUser( focussedAlbum.getOwnerProfile().getUser() );

        this.focussedAlbum = focussedAlbum;
    }

    /**
     * @return A new {@link SecurityToken} that represents the context of the currently active user.
     */
    public SecurityToken newToken() {

        return new SecurityToken( getActiveUser() );
    }
}
