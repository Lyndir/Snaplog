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
import com.lyndir.lhunath.lib.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


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
        IModel<String> tabTitle();
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
    static class AboutTab implements SnaplogTab<AboutTabPanel, AboutTabState> {

        public static final AboutTab instance = new AboutTab();

        static final Logger logger = Logger.get( AboutTab.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

        /**
         * {@inheritDoc}
         */
        @Override
        public IModel<String> getTitle() {

            return msgs.tabTitle();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AboutTabPanel getPanel(final String panelId) {

            return new AboutTabPanel( panelId );
        }

        @Override
        public Class<AboutTabPanel> getPanelClass() {

            return AboutTabPanel.class;
        }

        @Override
        public AboutTabState getState(final String fragment) {

            return new AboutTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isVisible() {

            return !SnaplogSession.get().isAuthenticated();
        }

        @Override
        public List<? extends SnaplogTool> listTools(final AboutTabPanel panel) {

            return ImmutableList.of();
        }

        @Override
        public String getTabFragment() {

            return "about";
        }

        @Override
        public AboutTabState buildFragmentState(final AboutTabPanel panel) {

            return new AboutTabState();
        }

        @Override
        public void applyFragmentState(final AboutTabPanel panel, final AboutTabState state)
                throws IncompatibleStateException {

            // No state.
        }
    }


    public static class AboutTabState extends AbstractFragmentState {

        public AboutTabState() {

        }

        public AboutTabState(final String fragment) {

            super( fragment );
        }

        @Override
        protected String getTabFragment() {

            return AboutTab.instance.getTabFragment();
        }
    }
}
