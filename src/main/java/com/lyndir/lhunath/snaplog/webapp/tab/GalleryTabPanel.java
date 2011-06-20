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
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.wayward.component.GenericPanel;
import com.lyndir.lhunath.opal.wayward.i18n.BooleanKeyAppender;
import com.lyndir.lhunath.opal.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.opal.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.media.Tag;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.model.service.*;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.tab.model.GalleryTabModels;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import com.lyndir.lhunath.snaplog.webapp.view.AbstractTagsView;
import com.lyndir.lhunath.snaplog.webapp.view.MediaView;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jetbrains.annotations.NotNull;


/**
 * <h2>{@link GalleryTabPanel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 1, 2010</i> </p>
 *
 * @author lhunath
 */
public class GalleryTabPanel extends GenericPanel<GalleryTabModels> {

    static final Logger logger = Logger.get( GalleryTabPanel.class );
    static final Messages msgs = MessagesFactory.create( Messages.class );

    static final int TAGS_PER_PAGE = 5;

    @Inject
    UserService userService;

    @Inject
    TagService tagService;

    @Inject
    SecurityService securityService;

    AbstractTagsView tags;

    /**
     * Create a new {@link GalleryTabPanel} instance.
     *
     * @param id        The wicket ID that will hold the {@link GalleryTabPanel}.
     * @param userModel The user whose gallery to show.
     */
    public GalleryTabPanel(final String id) {

        super( id, new GalleryTabModels( new IModel<User>() {

            @Override
            public void detach() {

            }

            @Override
            public User getObject() {

                return SnaplogSession.get().getFocusedUser();
            }

            @Override
            public void setObject(final User object) {

                SnaplogSession.get().setFocusedUser( object );
            }
        } ).getModel() );

        // Page info
        add( new Label( "tagsTitleUsername", getModelObject().decoratedUsername() ) );
        add( new Label( "tagsHelp", msgs.tagsHelp( new LoadableDetachableModel<Boolean>() {

            @Override
            protected Boolean load() {

                return SnaplogSession.get().isAuthenticated();
            }
        }, new LoadableDetachableModel<Boolean>() {

            @Override
            protected Boolean load() {

                return ObjectUtils.isEqual( getModelObject().getObject(), SnaplogSession.get().getActiveUser() );
            }
        }, new LoadableDetachableModel<Boolean>() {

            @Override
            protected Boolean load() {

                return tags.getItemCount() == 0;
            }
        }, getModelObject().username() ) ).setEscapeModelStrings( false ) );

        // List of tags
        // TODO: Make this data view top-level to provide Source enumeration elsewhere.
        add( tags = new AbstractTagsView( "tags", getModelObject(), TAGS_PER_PAGE ) {

            @Override
            protected void populateItem(final Item<Tag> tagItem) {

                tagItem.add( new AjaxLink<Tag>( "link", tagItem.getModel() ) {

                    {
                        add( new MediaView( "cover", cover( getModel() ), Quality.THUMBNAIL, false ) );
                        add( new Label( "title", getModelObject().getName() ) );
                        // TODO: Fix HTML injection.
                        add( new Label( "description", getModelObject().getDescription() ).setEscapeModelStrings( false ) );
                    }

                    @Override
                    public void onClick(final AjaxRequestTarget target) {

                        Tab.TAG.activateWithState( new TagTabPanel.TagTabState( getModelObject() ) );
                    }
                } );
            }
        } );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link GalleryTabPanel}.
         */
        IModel<String> tabTitle();

        /**
         * @param authenticated <code>true</code>: The current user has authenticated himself.<br> <code>false</code>: The current user has
         *                      not identified himself.
         * @param owned         <code>true</code>: The current user is the owner of the tags in the gallery.
         * @param empty         <code>true</code>: There are no tags to show.  Either the focused user has no tags or the active user
         *                      (or the public) has no sufficient permission to see any of them.
         * @param username      The name of the user whose gallery is being viewed.
         *
         * @return A text that explains that the visible gallery belongs to the current user.
         */
        IModel<String> tagsHelp(@BooleanKeyAppender(y = "auth", n = "anon") IModel<Boolean> authenticated,
                                  @BooleanKeyAppender(y = "own", n = "another") IModel<Boolean> owned,
                                  @BooleanKeyAppender(y = "empty") IModel<Boolean> empty, IModel<String> username);
    }


    /**
     * <h2>{@link GalleryTab}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> [description / usage]. </p>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class GalleryTab implements SnaplogTab<GalleryTabPanel, GalleryTabState> {

        public static final GalleryTab instance = new GalleryTab();

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
        public Class<GalleryTabPanel> getContentPanelClass() {

            return GalleryTabPanel.class;
        }

        @NotNull
        @Override
        public GalleryTabState getState(@NotNull final String fragment) {

            return new GalleryTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isInNavigation() {

            return SnaplogSession.get().getFocusedUser() != null;
        }

        @Override
        public List<? extends SnaplogTool> listTools(final GalleryTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getTabFragment() {

            return "gallery";
        }

        @NotNull
        @Override
        public GalleryTabState buildFragmentState(@NotNull final GalleryTabPanel panel) {

            return new GalleryTabState( SnaplogSession.get().getFocusedUser() );
        }

        @Override
        public void applyFragmentState(@NotNull final GalleryTabPanel panel, @NotNull final GalleryTabState state)
                throws IncompatibleStateException {

            try {
                SnaplogSession.get().setFocusedUser( state.getUser() );
            }

            catch (UserNotFoundException e) {
                throw new IncompatibleStateException( e );
            }
        }
    }


    public static class GalleryTabState extends AbstractFragmentState {

        private final UserService userService = GuiceContext.getInstance( UserService.class );

        final String userName;

        public GalleryTabState() {

            userName = null;
        }

        public GalleryTabState(final String fragment) {

            super( fragment );

            userName = findFragment( 1 );
        }

        public GalleryTabState(final User user) {

            // Load fields and fragments from parameter.
            appendFragment( userName = user.getUserName() );
        }

        public User getUser()
                throws UserNotFoundException {

            return userService.getUserWithUserName( checkNotNull( userName, "Username must not be null in this state." ) );
        }
    }
}
