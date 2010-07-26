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
 * <h2>{@link PageExpiredErrorPage}<br> <sub>Page that shows up when the user navigates to a page when his session timeout has
 * expired.</sub></h2>
 *
 * <p> <i>Jun 10, 2009</i> </p>
 *
 * @author lhunath
 */
public class PageExpiredErrorPage extends LayoutPage {

    @Override
    protected void onBeforeRender() {

        getController().activateNewTab( PageExpiredErrorTab.instance );

        throw new RedirectToPageException( LayoutPage.class );
    }

    static class PageExpiredErrorTabPanel extends Panel {

        PageExpiredErrorTabPanel(final String id) {

            super( id );
        }
    }


    static class PageExpiredErrorTab implements SnaplogTab<PageExpiredErrorTabPanel, PageExpiredErrorState> {

        public static final PageExpiredErrorTab instance = new PageExpiredErrorTab();

        @Override
        public List<? extends SnaplogTool> listTools(final PageExpiredErrorTabPanel panel) {

            return ImmutableList.of();
        }

        @Override
        public String getTabFragment() {

            return "expired";
        }

        @Override
        public PageExpiredErrorState buildFragmentState(final PageExpiredErrorTabPanel panel) {

            return new PageExpiredErrorState();
        }

        @Override
        public void applyFragmentState(final PageExpiredErrorTabPanel panel, final PageExpiredErrorState state)
                throws IncompatibleStateException {

            // No state.
        }

        @Override
        public IModel<String> getTitle() {

            return ModelTemplates.unsupportedOperation();
        }

        @Override
        public PageExpiredErrorTabPanel getPanel(final String panelId) {

            return new PageExpiredErrorTabPanel( panelId );
        }

        @Override
        public boolean isVisible() {

            return true;
        }

        @Override
        public Class<PageExpiredErrorTabPanel> getPanelClass() {

            return PageExpiredErrorTabPanel.class;
        }

        @Override
        public PageExpiredErrorState getState(final String fragment) {

            return new PageExpiredErrorState( fragment );
        }
    }


    static class PageExpiredErrorState extends AbstractFragmentState {

        PageExpiredErrorState() {
        }

        PageExpiredErrorState(final String fragment) {
            super( fragment );
        }

        @Override
        protected String getTabFragment() {

            return PageExpiredErrorTab.instance.getTabFragment();
        }
    }
}
