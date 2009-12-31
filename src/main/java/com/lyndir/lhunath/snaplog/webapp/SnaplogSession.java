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

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

import com.lyndir.lhunath.snaplog.model.User;


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

    private User activeUser;


    public SnaplogSession(Request request) {

        super( request );
    }

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
     * @param activeUser
     *            The activeUser of this {@link SnaplogSession}.
     */
    public void setActiveUser(User activeUser) {

        this.activeUser = activeUser;
    }
}
