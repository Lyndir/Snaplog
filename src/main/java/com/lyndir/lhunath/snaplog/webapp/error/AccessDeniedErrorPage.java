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
package com.lyndir.lhunath.snaplog.webapp.error;

import org.apache.wicket.markup.html.panel.Panel;

import com.lyndir.lhunath.snaplog.webapp.page.MessagePage;


/**
 * <h2>{@link AccessDeniedErrorPage}<br>
 * <sub>Page that is shown when the user is denied access to a resource.</sub></h2>
 * 
 * <p>
 * <i>Jun 10, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class AccessDeniedErrorPage extends MessagePage {

    /**
     * Create a new {@link AccessDeniedErrorPage} instance.
     */
    public AccessDeniedErrorPage() {

        addOrReplace( new AccessDeniedErrorPanel( "contentPanel" ) );
    }


    private static class AccessDeniedErrorPanel extends Panel {

        AccessDeniedErrorPanel(String id) {

            super( id );
        }
    }
}
