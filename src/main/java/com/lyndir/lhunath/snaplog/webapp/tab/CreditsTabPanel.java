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

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.logging.exception.TodoException;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link CreditsTabPanel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 1, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class CreditsTabPanel extends Panel {

    /**
     * Create a new {@link CreditsTabPanel} instance.
     *
     * @param id The wicket ID that will hold the {@link CreditsTabPanel}.
     */
    public CreditsTabPanel(final String id) {

        super( id );
    }


    interface Messages {

    }


    /**
     * <h2>{@link CreditsTab}<br>
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
    static class CreditsTab implements ITab {

        static final Logger logger = Logger.get( CreditsTab.class );
        static final Messages msgs = MessagesFactory.create( Messages.class, CreditsTabPanel.class );


        /**
         * {@inheritDoc}
         */
        @Override
        public IModel<String> getTitle() {

            return new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    throw new TodoException();
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Panel getPanel(final String panelId) {

            return new CreditsTabPanel( panelId );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isVisible() {

            return true;
        }
    }
}
