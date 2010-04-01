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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.lyndir.lhunath.lib.system.localization.UseKey;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;


/**
 * <h2>{@link AboutTabPanel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 1, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class AboutTabPanel extends Panel {

    /**
     * Create a new {@link AboutTabPanel} instance.
     * 
     * @param id
     *            The wicket ID that will hold the {@link AboutTabPanel}.
     */
    public AboutTabPanel(String id) {

        super( id );
    }


    static interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link AboutTabPanel}.
         */
        @UseKey
        String aboutTab();
    }
}


/**
 * <h2>{@link AboutTab}<br>
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
class AboutTab implements SnaplogTab {

    static final Logger logger = Logger.get( AboutTab.class );
    static final AboutTabPanel.Messages msgs = MessagesFactory.create( AboutTabPanel.Messages.class,
                                                                       AboutTabPanel.class );


    /**
     * {@inheritDoc}
     */
    @Override
    public IModel<String> getTitle() {

        return new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return msgs.aboutTab();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Panel getPanel(String panelId) {

        return new AboutTabPanel( panelId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Panel getTools(String panelId) {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return !SnaplogSession.get().isAuthenticated();
    }
}
