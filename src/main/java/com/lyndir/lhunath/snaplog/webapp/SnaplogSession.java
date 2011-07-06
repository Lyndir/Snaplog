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

import com.google.common.base.Objects;
import com.lyndir.lhunath.snaplog.security.SnaplogST;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
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

        if (focusedUser == null)
            // Focus on the active user if not focusing on anyone.
            setFocusedUser( activeUser );

        return focusedUser;
    }

    /**
     * @param focusedUser The focusedUser of this {@link SnaplogSession}.
     */
    public void setFocusedUser(final User focusedUser) {

        this.focusedUser = focusedUser;
    }

    /**
     * @return A new {@link SnaplogST} that represents the context of the currently active user.
     */
    public SnaplogST newToken() {

        return new SnaplogST( getActiveUser() );
    }
}
