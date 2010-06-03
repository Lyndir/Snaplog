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

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.localization.UseKey;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link AboutTabPanel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 1, 2010</i> </p>
 *
 * @author lhunath
 */
public class AboutTabPanel extends Panel {

    /**
     * Create a new {@link AboutTabPanel} instance.
     *
     * @param id The wicket ID that will hold the {@link AboutTabPanel}.
     */
    public AboutTabPanel(final String id) {

        super( id );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link AboutTabPanel}.
         */
        @UseKey
        String aboutTab();
    }


    /**
     * <h2>{@link AboutTab}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> [description / usage]. </p>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class AboutTab implements SnaplogTab {

        static final Logger logger = Logger.get( AboutTab.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

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
        public Panel getPanel(final String panelId) {

            return new AboutTabPanel( panelId );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isVisible() {

            return !SnaplogSession.get().isAuthenticated();
        }

        @Override
        public List<? extends SnaplogTool> listTools() {

            return ImmutableList.of();
        }

        @Override
        public String getFragment() {

            return "about";
        }

        @Override
        public void applyFragmentState(final Panel panel, final String... arguments) {

            // No state.
        }
    }
}
