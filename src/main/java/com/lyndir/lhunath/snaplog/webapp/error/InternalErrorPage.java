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

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.google.gson.Gson;
import com.lyndir.lhunath.lib.system.util.Utils;
import com.lyndir.lhunath.snaplog.webapp.SnaplogWebApplication;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;


/**
 * <h2>{@link InternalErrorPage}<br>
 * <sub>Page that is shown when an uncaught exception occurs.</sub></h2>
 * 
 * <p>
 * <i>Jun 10, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class InternalErrorPage extends LayoutPage {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Panel getInitialContentPanel(String wicketId) {

        return new InternalErrorPanel( wicketId );
    }


    private static class InternalErrorPanel extends Panel {

        InternalErrorPanel(String id) {

            super( id );

            Issue issue = getMetaData( SnaplogWebApplication.METADATA_RUNTIME_EXCEPTION_ISSUE );

            // TODO: Store this data somewhere for reference.
            String issueCode = Utils.getMD5( new Gson().toJson( issue ) );

            add( new TextField<String>( "issueCode", new Model<String>( issueCode ) ) );
        }
    }
}
