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
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.component.GenericPanel;
import com.lyndir.lhunath.opal.wayward.component.WicketUtils;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.wayward.navigation.*;
import com.lyndir.lhunath.opal.wayward.state.TabActivator;
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
import org.jetbrains.annotations.NotNull;


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

    private static class NewUserTabDescriptor implements SnaplogTabDescriptor<NewUserTabPanel, NewUserTabState> {

        public static final NewUserTabDescriptor instance = new NewUserTabDescriptor();

        static final Messages msgs = MessagesFactory.create( Messages.class );

        @Override
        public List<? extends SnaplogTool> listTools(final NewUserTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getFragment() {

            return "newUser";
        }

        @NotNull
        @Override
        public NewUserTabState newState(@NotNull final NewUserTabPanel panel) {

            return new NewUserTabState();
        }

        @NotNull
        @Override
        public Class<NewUserTabPanel> getContentPanelClass() {

            return NewUserTabPanel.class;
        }

        @NotNull
        @Override
        public NewUserTabState newState(@NotNull final String fragment) {

            return new NewUserTabState( fragment );
        }

        @NotNull
        @Override
        public IModel<String> getTitle() {

            return msgs.tabTitle();
        }

        @Override
        public boolean shownInNavigation() {

            return false;
        }
    }


    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link NewUserTabPanel}.
         */
        IModel<String> tabTitle();
    }


    public static class NewUserTabState extends AbstractTabState<NewUserTabPanel> {

        public NewUserTabState() {
        }

        public NewUserTabState(final String fragment) {

            super( fragment );
        }

        @Override
        public void apply(@NotNull final NewUserTabPanel panel)
                throws IncompatibleStateException {

            // No state.
        }
    }


    /**
     * <h2>{@link NewUserTabActivator}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> <i>Mar 21, 2010</i> </p>
     *
     * @author lhunath
     */
    public static class NewUserTabActivator extends TabActivator<NewUserTabPanel, NewUserTabState, NewUserTabDescriptor> {

        /**
         * Create a new {@link NewUserTabActivator} instance.
         */
        public NewUserTabActivator() {

            super( NewUserTabDescriptor.instance );
        }

        @Override
        protected TabController findController() {

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
