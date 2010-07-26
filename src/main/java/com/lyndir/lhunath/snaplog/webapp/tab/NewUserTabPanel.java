/*
 *   Copyright 2010, Maarten Billemont
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
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.component.WicketUtils;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.FragmentNavigationListener;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.lib.wayward.state.TabActivator;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.error.UsernameTakenException;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.page.model.NewUserPanelModels;
import com.lyndir.lhunath.snaplog.webapp.page.model.NewUserPanelModels.NewUserFormModels;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import net.link.safeonline.sdk.auth.filter.LoginManager;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link NewUserTabPanel}<br> <sub>Content panel for the {@link NewUserTabPanel}.</sub></h2>
 *
 * <p> <i>Mar 17, 2010</i> </p>
 *
 * @author lhunath
 */
public class NewUserTabPanel extends GenericPanel<NewUserPanelModels> {

    static final Logger logger = Logger.get( NewUserTabPanel.class );

    @Inject
    UserService userService;

    NewUserTabPanel(final String id) {

        super( id, new NewUserPanelModels().getModel() );

        add( new Form<NewUserFormModels>( "newUserForm", getModelObject().newUserForm().getModel() ) {

            {
                add( new RequiredTextField<String>( "userName", getModelObject().userName() ) );
            }

            @Override
            protected void onSubmit() {

                logger.dbg( "Submitting newUserForm" );
                String linkID = checkNotNull( LoginManager.findUserId( WicketUtils.getServletRequest() ), //
                                              "LinkID identifier must not be null." );

                try {
                    logger.dbg( "Submitting registering user" );
                    userService.registerUser( new LinkID( linkID ), getModelObject().userName().getObject() );
                    logger.dbg( "Done!" );
                }

                catch (UsernameTakenException e) {
                    logger.dbg( e, "Failed" );
                    error( e.getLocalizedMessage() );
                }
            }
        } );
    }

    private static class NewUserTab implements SnaplogTab<NewUserTabPanel, NewUserTabState> {

        public static final NewUserTab instance = new NewUserTab();

        static final Messages msgs = MessagesFactory.create( Messages.class );

        @Override
        public List<? extends SnaplogTool> listTools(final NewUserTabPanel panel) {

            return ImmutableList.of();
        }

        @Override
        public String getTabFragment() {

            return "newUser";
        }

        @Override
        public NewUserTabState buildFragmentState(final NewUserTabPanel panel) {

            return new NewUserTabState();
        }

        @Override
        public void applyFragmentState(final NewUserTabPanel panel, final NewUserTabState state)
                throws IncompatibleStateException {

            // No state.
        }

        @Override
        public NewUserTabPanel getPanel(final String panelId) {

            return new NewUserTabPanel( panelId );
        }

        @Override
        public Class<NewUserTabPanel> getPanelClass() {

            return NewUserTabPanel.class;
        }

        @Override
        public NewUserTabState getState(final String fragment) {

            return new NewUserTabState( fragment );
        }

        @Override
        public IModel<String> getTitle() {

            return msgs.newUserTab();
        }

        @Override
        public boolean isVisible() {

            return false;
        }
    }


    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link NewUserTabPanel}.
         */
        IModel<String> newUserTab();
    }


    public static class NewUserTabState extends AbstractFragmentState {

        public NewUserTabState() {
        }

        public NewUserTabState(final String fragment) {

            super( fragment );
        }

        @Override
        protected String getTabFragment() {

            return NewUserTab.instance.getTabFragment();
        }
    }


    /**
     * <h2>{@link NewUserTabActivator}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> <i>Mar 21, 2010</i> </p>
     *
     * @author lhunath
     */
    public static class NewUserTabActivator extends TabActivator<NewUserTabPanel, NewUserTabState, NewUserTab> {

        /**
         * Create a new {@link NewUserTabActivator} instance.
         */
        public NewUserTabActivator() {

            super( NewUserTab.instance );
        }

        @Override
        protected FragmentNavigationListener.Controller<? super NewUserTabPanel, ? super NewUserTabState, ? super NewUserTab> findController() {

            return LayoutPage.getController();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isNecessary() {

            return LoginManager.isAuthenticated( WicketUtils.getServletRequest() ) && !SnaplogSession.get().isAuthenticated();
        }
    }
}
