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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.wayward.model.ModelTemplates;
import com.lyndir.lhunath.lib.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.Issue;
import com.lyndir.lhunath.snaplog.error.IssueNotFoundException;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.IssueService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link InternalErrorPage}<br> <sub>Page that is shown when an uncaught exception occurs.</sub></h2>
 *
 * <p> <i>Jun 10, 2009</i> </p>
 *
 * @author lhunath
 */
public class InternalErrorPage extends LayoutPage {

    final Issue issue;

    public InternalErrorPage(final Issue issue) {

        this.issue = issue;
    }

    @Override
    protected void onBeforeRender() {

        try {
            if (issue != null)
                getController().activateTabWithState( InternalErrorTab.instance, new InternalErrorState( issue ) );
            else {
                // No issue; odd - something must have gone wrong while building error context. Just display a new error page without state.
                logger.wrn( "InternalErrorPage loaded without issue." );
                getController().activateNewTab( InternalErrorTab.instance );
            }
        }
        catch (IncompatibleStateException e) {
            Session.get().error( e.getLocalizedMessage() );

            getController().activateNewTab( InternalErrorTab.instance );
        }

        super.onBeforeRender();
    }

    static class InternalErrorTabPanel extends Panel {

        private Issue issue;

        InternalErrorTabPanel(final String id) {

            super( id );

            add( new TextField<String>( "issueCode", new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    return issue.getIssueCode();
                }
            } ) {

                @Override
                public boolean isVisible() {

                    return super.isVisible() && issue != null;
                }
            } );
        }

        Issue getIssue() {

            return issue;
        }

        void setIssue(final Issue issue) {

            this.issue = issue;
        }
    }


    static class InternalErrorTab implements SnaplogTab<InternalErrorTabPanel, InternalErrorState> {

        public static final InternalErrorTab instance = new InternalErrorTab();

        @Override
        public List<? extends SnaplogTool> listTools(final InternalErrorTabPanel panel) {

            return ImmutableList.of();
        }

        @Override
        public String getTabFragment() {

            return "error";
        }

        @Override
        public InternalErrorState buildFragmentState(final InternalErrorTabPanel panel) {

            if (panel.getIssue() == null)
                return new InternalErrorState();

            return new InternalErrorState( panel.getIssue() );
        }

        @Override
        public void applyFragmentState(final InternalErrorTabPanel panel, final InternalErrorState state)
                throws IncompatibleStateException {

            try {
                panel.setIssue( state.findIssue() );
            }

            catch (IssueNotFoundException e) {
                throw new IncompatibleStateException( e );
            }
            catch (PermissionDeniedException e) {
                throw new IncompatibleStateException( e );
            }
        }

        @Override
        public IModel<String> getTitle() {

            return ModelTemplates.unsupportedOperation();
        }

        @Override
        public InternalErrorTabPanel getPanel(final String panelId) {

            return new InternalErrorTabPanel( panelId );
        }

        @Override
        public boolean isVisible() {

            return true;
        }

        @Override
        public Class<InternalErrorTabPanel> getPanelClass() {

            return InternalErrorTabPanel.class;
        }

        @Override
        public InternalErrorState getState(final String fragment) {

            return new InternalErrorState( fragment );
        }
    }


    static class InternalErrorState extends AbstractFragmentState {

        private final IssueService issueService = GuiceContext.getInstance( IssueService.class );

        private final String issueCode;

        InternalErrorState() {

            issueCode = null;
        }

        InternalErrorState(final String fragment) {

            super( fragment );

            issueCode = findFragment( 1 );
        }

        InternalErrorState(final Issue issue) {

            checkNotNull( issue, "Issue can't be null when creating state based on it." );

            appendFragment( issueCode = issue.getIssueCode() );
        }

        public Issue findIssue()
                throws IssueNotFoundException, PermissionDeniedException {

            if (issueCode == null)
                return null;

            return issueService.getIssue( SnaplogSession.get().newToken(), issueCode );
        }

        @Override
        protected String getTabFragment() {

            return InternalErrorTab.instance.getTabFragment();
        }
    }
}
