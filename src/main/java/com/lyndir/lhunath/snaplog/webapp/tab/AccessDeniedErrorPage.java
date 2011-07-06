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
import com.lyndir.lhunath.opal.wayward.component.RedirectToPageException;
import com.lyndir.lhunath.opal.wayward.model.Models;
import com.lyndir.lhunath.opal.wayward.navigation.AbstractTabState;
import com.lyndir.lhunath.opal.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.NotNull;


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

        getController().activateNewTab( AccessDeniedErrorTabDescriptor.instance );

        throw new RedirectToPageException( LayoutPage.class );
    }

    static class AccessDeniedErrorTabPanel extends Panel {

        AccessDeniedErrorTabPanel(final String id) {

            super( id );
        }
    }


    static class AccessDeniedErrorTabDescriptor implements SnaplogTabDescriptor<AccessDeniedErrorTabPanel, AccessDeniedErrorState> {

        public static final AccessDeniedErrorTabDescriptor instance = new AccessDeniedErrorTabDescriptor();

        @Override
        public List<? extends SnaplogTool> listTools(final AccessDeniedErrorTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getFragment() {

            return "denied";
        }

        @NotNull
        @Override
        public AccessDeniedErrorState newState(@NotNull final AccessDeniedErrorTabPanel panel) {

            return new AccessDeniedErrorState();
        }

        @NotNull
        @Override
        public IModel<String> getTitle() {

            return Models.unsupportedOperation();
        }

        @Override
        public boolean shownInNavigation() {

            return true;
        }

        @NotNull
        @Override
        public Class<AccessDeniedErrorTabPanel> getContentPanelClass() {

            return AccessDeniedErrorTabPanel.class;
        }

        @NotNull
        @Override
        public AccessDeniedErrorState newState(@NotNull final String fragment) {

            return new AccessDeniedErrorState( fragment );
        }
    }


    static class AccessDeniedErrorState extends AbstractTabState<AccessDeniedErrorTabPanel> {

        AccessDeniedErrorState() {

        }

        AccessDeniedErrorState(final String fragment) {

            super( fragment );
        }

        @Override
        public void apply(@NotNull final AccessDeniedErrorTabPanel panel)
                throws IncompatibleStateException {

            // No state.
        }
    }
}
