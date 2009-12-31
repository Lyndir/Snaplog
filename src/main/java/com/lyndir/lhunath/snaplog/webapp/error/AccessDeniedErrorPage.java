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
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Jun 10, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class AccessDeniedErrorPage extends MessagePage {

    @Override
    protected Panel getDefaultPanel(String wicketId) {

        return new AccessDeniedErrorPanel( wicketId );
    }
}


class AccessDeniedErrorPanel extends Panel {

    public AccessDeniedErrorPanel(String id) {

        super( id );
    }
}
