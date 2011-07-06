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
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.wayward.navigation.AbstractTabState;
import com.lyndir.lhunath.opal.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.NotNull;


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
        IModel<String> tabTitle();
    }


    /**
     * <h2>{@link AboutTabDescriptor}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> [description / usage]. </p>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class AboutTabDescriptor implements SnaplogTabDescriptor<AboutTabPanel, AboutTabState> {

        public static final AboutTabDescriptor instance = new AboutTabDescriptor();

        static final Logger logger = Logger.get( AboutTabDescriptor.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

        /**
         * {@inheritDoc}
         */
        @NotNull
        @Override
        public IModel<String> getTitle() {

            return msgs.tabTitle();
        }

        @NotNull
        @Override
        public Class<AboutTabPanel> getContentPanelClass() {

            return AboutTabPanel.class;
        }

        @NotNull
        @Override
        public AboutTabState newState(@NotNull final String fragment) {

            return new AboutTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean shownInNavigation() {

            return !SnaplogSession.get().isAuthenticated();
        }

        @Override
        public List<? extends SnaplogTool> listTools(final AboutTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getFragment() {

            return "about";
        }

        @NotNull
        @Override
        public AboutTabState newState(@NotNull final AboutTabPanel panel) {

            return new AboutTabState();
        }
    }


    public static class AboutTabState extends AbstractTabState<AboutTabPanel> {

        public AboutTabState() {

        }

        public AboutTabState(final String fragment) {

            super( fragment );
        }

        @Override
        public void apply(@NotNull final AboutTabPanel panel)
                throws IncompatibleStateException {

            // No state.
        }
    }
}
