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

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

import com.google.common.base.Objects;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;


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

    private Tab                 activeTab;
    private User                activeUser;
    private User                focussedUser;
    private Album               focussedAlbum;


    /**
     * {@inheritDoc}
     * 
     * @param request
     *            The {@link Request} that started the session.
     */
    public SnaplogSession(Request request) {

        super( request );
    }

    /**
     * @return {@link Session#get()}
     */
    public static SnaplogSession get() {

        return (SnaplogSession) Session.get();
    }

    /**
     * @return The activeTab of this {@link SnaplogSession}.
     */
    public Tab getActiveTab() {

        return activeTab;
    }

    /**
     * @param activeTab
     *            The activeTab of this {@link SnaplogSession}.
     */
    public void setActiveTab(Tab activeTab) {

        this.activeTab = activeTab;
    }

    /**
     * @return The activeUser of this {@link SnaplogSession}.
     */
    public User getActiveUser() {

        return activeUser;
    }

    /**
     * @param activeUser
     *            The activeUser of this {@link SnaplogSession}.
     */
    public void setActiveUser(User activeUser) {

        if (Objects.equal( getActiveUser(), activeUser ))
            return;

        logger.inf( "Session user identification changed from: %s, to: %s", //
                    getActiveUser(), activeUser );

        this.activeUser = activeUser;
    }

    /**
     * @return The focussedUser of this {@link SnaplogSession}.
     */
    public User getFocussedUser() {

        if (focussedAlbum != null)
            // These SHOULD always match if an album is focussed.
            checkState( focussedAlbum.getUser().equals( focussedUser ) );
        if (focussedUser == null)
            // Focus on the active user if not focusing on anyone.
            setFocussedUser( activeUser );

        return focussedUser;
    }

    /**
     * @param focussedUser
     *            The focussedUser of this {@link SnaplogSession}.
     */
    public void setFocussedUser(User focussedUser) {

        if (focussedAlbum != null && !focussedAlbum.getUser().equals( focussedUser ))
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
            checkState( focussedAlbum.getUser().equals( focussedUser ) );

        return focussedAlbum;
    }

    /**
     * @param focussedAlbum
     *            The focussedAlbum of this {@link SnaplogSession}.
     */
    public void setFocussedAlbum(Album focussedAlbum) {

        if (focussedAlbum != null)
            // Focusing a specific album; set focussed user to the album owner.
            setFocussedUser( focussedAlbum.getUser() );

        this.focussedAlbum = focussedAlbum;
    }
}
