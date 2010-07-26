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
import com.lyndir.lhunath.lib.wayward.component.RedirectToPageException;
import com.lyndir.lhunath.lib.wayward.model.ModelTemplates;
import com.lyndir.lhunath.lib.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link AccessDeniedErrorPage}<br> <sub>Page that is shown when the user is denied access to a resource.</sub></h2>
 *
 * <p> <i>Jun 10, 2009</i> </p>
 *
 * @author lhunath
 */
public class AccessDeniedErrorPage extends LayoutPage {

    @Override
    protected void onBeforeRender() {

        getController().activateNewTab( AccessDeniedErrorTab.instance );

        throw new RedirectToPageException( LayoutPage.class );
    }

    static class AccessDeniedErrorTabPanel extends Panel {

        AccessDeniedErrorTabPanel(final String id) {

            super( id );
        }
    }


    static class AccessDeniedErrorTab implements SnaplogTab<AccessDeniedErrorTabPanel, AccessDeniedErrorState> {

        public static final AccessDeniedErrorTab instance = new AccessDeniedErrorTab();

        @Override
        public List<? extends SnaplogTool> listTools(final AccessDeniedErrorTabPanel panel) {

            return ImmutableList.of();
        }

        @Override
        public String getTabFragment() {

            return "denied";
        }

        @Override
        public AccessDeniedErrorState buildFragmentState(final AccessDeniedErrorTabPanel panel) {

            return new AccessDeniedErrorState();
        }

        @Override
        public void applyFragmentState(final AccessDeniedErrorTabPanel panel, final AccessDeniedErrorState state)
                throws IncompatibleStateException {

            // No state.
        }

        @Override
        public IModel<String> getTitle() {

            return ModelTemplates.unsupportedOperation();
        }

        @Override
        public AccessDeniedErrorTabPanel getPanel(final String panelId) {

            return new AccessDeniedErrorTabPanel( panelId );
        }

        @Override
        public boolean isVisible() {

            return true;
        }

        @Override
        public Class<AccessDeniedErrorTabPanel> getPanelClass() {

            return AccessDeniedErrorTabPanel.class;
        }

        @Override
        public AccessDeniedErrorState getState(final String fragment) {

            return new AccessDeniedErrorState( fragment );
        }
    }


    static class AccessDeniedErrorState extends AbstractFragmentState {

        AccessDeniedErrorState() {
        }

        AccessDeniedErrorState(final String fragment) {
            super( fragment );
        }

        @Override
        protected String getTabFragment() {

            return AccessDeniedErrorTab.instance.getTabFragment();
        }
    }}
