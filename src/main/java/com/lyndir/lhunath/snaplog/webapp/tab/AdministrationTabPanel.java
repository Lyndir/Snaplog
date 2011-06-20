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
import com.lyndir.lhunath.opal.system.localization.UseKey;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.wayward.navigation.AbstractFragmentState;
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
        @UseKey
        IModel<String> tabTitle();
    }


    /**
     * <h2>{@link AdministrationTab}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> [description / usage]. </p>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class AdministrationTab implements SnaplogTab<AdministrationTabPanel, AdministrationTabState> {

        public static final AdministrationTab instance = new AdministrationTab();

        static final Logger logger = Logger.get( AdministrationTab.class );
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
        public AdministrationTabState getState(@NotNull final String fragment) {

            return new AdministrationTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isInNavigation() {

            return SnaplogSession.get().isAuthenticated();
        }

        @Override
        public List<? extends SnaplogTool> listTools(final AdministrationTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getTabFragment() {

            return "admin";
        }

        @NotNull
        @Override
        public AdministrationTabState buildFragmentState(@NotNull final AdministrationTabPanel panel) {

            return new AdministrationTabState();
        }

        @Override
        public void applyFragmentState(@NotNull final AdministrationTabPanel panel, @NotNull final AdministrationTabState state)
                throws IncompatibleStateException {

            // No state.
        }
    }


    public static class AdministrationTabState extends AbstractFragmentState {

        public AdministrationTabState() {

        }

        public AdministrationTabState(final String fragment) {

            super( fragment );
        }
    }
}
