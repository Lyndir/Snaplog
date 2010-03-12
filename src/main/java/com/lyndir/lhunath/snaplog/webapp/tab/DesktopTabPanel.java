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
package com.lyndir.lhunath.snaplog.webapp.tab;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.messages.Messages;


/**
 * <h2>{@link DesktopTabPanel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 1, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class DesktopTabPanel extends Panel {

    /**
     * Create a new {@link DesktopTabPanel} instance.
     * 
     * @param id
     *            The wicket ID that will hold the {@link DesktopTabPanel}.
     */
    public DesktopTabPanel(String id) {

        super( id );
    }
}


/**
 * <h2>{@link DesktopTabPanel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>May 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
class DesktopTab implements ITab {

    static final Logger logger = Logger.get( DesktopTab.class );
    Messages            msgs   = LocalizerFactory.getLocalizer( Messages.class );


    /**
     * {@inheritDoc}
     */
    @Override
    public IModel<String> getTitle() {

        return new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return msgs.desktopTab();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Panel getPanel(String panelId) {

        return new DesktopTabPanel( panelId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return true;
    }
}
