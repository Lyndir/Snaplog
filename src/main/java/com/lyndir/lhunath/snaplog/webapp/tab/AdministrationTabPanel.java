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
 * <h2>{@link AdministrationTabPanel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 1, 2010</i> </p>
 *
 * @author lhunath
 */
public class AdministrationTabPanel extends Panel {

    /**
     * Create a new {@link AdministrationTabPanel} instance.
     *
     * @param id The wicket ID that will hold the {@link AdministrationTabPanel}.
     */
    public AdministrationTabPanel(final String id) {

        super( id );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link AdministrationTabPanel}
         */
        IModel<String> tabTitle();
    }


    /**
     * <h2>{@link AdministrationTabDescriptor}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> [description / usage]. </p>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class AdministrationTabDescriptor implements SnaplogTabDescriptor<AdministrationTabPanel, AdministrationTabState> {

        public static final AdministrationTabDescriptor instance = new AdministrationTabDescriptor();

        static final Logger logger = Logger.get( AdministrationTabDescriptor.class );
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
        public Class<AdministrationTabPanel> getContentPanelClass() {

            return AdministrationTabPanel.class;
        }

        @NotNull
        @Override
        public AdministrationTabState newState(@NotNull final String fragment) {

            return new AdministrationTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean shownInNavigation() {

            return SnaplogSession.get().isAuthenticated();
        }

        @Override
        public List<? extends SnaplogTool> listTools(final AdministrationTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getFragment() {

            return "admin";
        }

        @NotNull
        @Override
        public AdministrationTabState newState(@NotNull final AdministrationTabPanel panel) {

            return new AdministrationTabState();
        }
    }

    public static class AdministrationTabState extends AbstractTabState<AdministrationTabPanel> {

        public AdministrationTabState() {

        }

        public AdministrationTabState(final String fragment) {

            super( fragment );
        }

        @Override
        public void apply(@NotNull final AdministrationTabPanel panel)
                throws IncompatibleStateException {

            // No state.
        }
    }
}
