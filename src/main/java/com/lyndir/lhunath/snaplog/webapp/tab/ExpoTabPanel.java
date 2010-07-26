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
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.BooleanKeyAppender;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.tab.model.ExpoTabModels;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import com.lyndir.lhunath.snaplog.webapp.view.AbstractAlbumsView;
import com.lyndir.lhunath.snaplog.webapp.view.AbstractUsersView;
import com.lyndir.lhunath.snaplog.webapp.view.MediaView;
import com.lyndir.lhunath.snaplog.webapp.view.UserLink;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.AppendingStringBuffer;


/**
 * <h2>{@link ExpoTabPanel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 1, 2010</i> </p>
 *
 * @author lhunath
 */
public class ExpoTabPanel extends GenericPanel<ExpoTabModels> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    static final int USERS_PER_PAGE = 3;
    static final int ALBUMS_PER_PAGE = 5;

    @Inject
    UserService userService;

    @Inject
    AlbumService albumService;

    // TODO: Remove when <https://issues.apache.org/jira/browse/WICKET-2797> is fixed.
    final Form<?> searchForm;

    /**
     * Create a new {@link ExpoTabPanel} instance.
     *
     * @param id The wicket ID that will hold the {@link ExpoTabPanel}.
     */
    public ExpoTabPanel(final String id) {

        super( id, new ExpoTabModels().getModel() );
        getModelObject().attach( this );

        add( new Label( "usersHelp", getModelObject().usersHelp() ) );
        add( new AbstractUsersView( "users", USERS_PER_PAGE ) {

            @Override
            protected void populateItem(final Item<User> userItem) {

                userItem.add( new UserLink( "userName", userItem.getModel() ) );
                userItem.add( new AbstractAlbumsView( "albums", userItem.getModel(), ALBUMS_PER_PAGE ) {

                    @Override
                    protected void populateItem(final Item<Album> albumItem) {

                        albumItem.add( new MediaView( "albumCover", cover( albumItem.getModel() ), Quality.THUMBNAIL, true ) {

                            @Override
                            protected void onClick(final AjaxRequestTarget target) {

                                Tab.ALBUM.activateWithState( new AlbumTabPanel.AlbumTabState( getModelObject().getAlbum() ) );
                            }

                            @Override
                            protected String getCaptionString() {

                                return getModelObject().getAlbum().getName();
                            }
                        } );
                    }

                    @Override
                    public boolean isVisible() {

                        // userItem's visibility == the visibility of the albums view in it.
                        boolean visible = super.isVisible();
                        userItem.setVisible( visible );

                        return visible;
                    }
                } );
            }
        } );

        add( (searchForm = new Form<String>( "searchForm", new Model<String>() ) {

            final AbstractUsersView usersView;
            final AbstractAlbumsView albumsView;

            {
                // Search Query
                final IModel<String> queryModel = getModel();
                add( new RequiredTextField<String>( "query", queryModel ) );

                // Results
                add( new Label( "results", new LoadableDetachableModel<String>() {

                    @Override
                    protected String load() {

                        int resultCount = usersView.getItemCount() + albumsView.getItemCount();

                        if (resultCount == 0)
                            return msgs.noResults();
                        else if (resultCount == 1)
                            return msgs.singularResult( resultCount );
                        else
                            return msgs.multipleResults( resultCount );
                    }
                } ) {

                    @Override
                    public boolean isVisible() {

                        return queryModel.getObject() != null && queryModel.getObject().length() > 0;
                    }
                } );

                // Found Users
                add( usersView = new AbstractUsersView( "users", new IPredicate<User>() {

                    @Override
                    public boolean apply(final User input) {

                        // Applies for a user whose userName contains the search string (case insensitively).
                        return input != null && queryModel.getObject() != null && queryModel.getObject().length() > 0 && input.getUserName()
                                .toUpperCase()
                                .contains( queryModel.getObject().toUpperCase() );
                    }
                }, USERS_PER_PAGE ) {

                    @Override
                    protected void populateItem(final Item<User> userItem) {

                        userItem.add( new UserLink( "userName", userItem.getModel() ) );
                        userItem.add( new AbstractAlbumsView( "albums", userItem.getModel(), ALBUMS_PER_PAGE ) {

                            @Override
                            protected void populateItem(final Item<Album> albumItem) {

                                albumItem.add( new MediaView( "albumCover", cover( albumItem.getModel() ), Quality.THUMBNAIL, true ) {

                                    @Override
                                    protected void onClick(final AjaxRequestTarget target) {

                                        Tab.ALBUM.activateWithState( new AlbumTabPanel.AlbumTabState( getModelObject().getAlbum() ) );
                                    }

                                    @Override
                                    protected String getCaptionString() {

                                        return getModelObject().getAlbum().getName();
                                    }
                                } );
                            }
                        } );
                    }
                } );

                // Found Albums
                add( albumsView = new AbstractAlbumsView( "albums", new IPredicate<Album>() {

                    @Override
                    public boolean apply(final Album input) {

                        // Applies for an album whose name contains the search string (case insensitively).
                        return input != null && queryModel.getObject() != null && queryModel.getObject().length() > 0 && input.getName()
                                .toUpperCase()
                                .contains( queryModel.getObject().toUpperCase() );
                    }
                }, ALBUMS_PER_PAGE ) {

                    @Override
                    protected void populateItem(final Item<Album> albumItem) {

                        albumItem.add( new MediaView( "cover", cover( albumItem.getModel() ), Quality.THUMBNAIL, true ) {

                            @Override
                            public void onClick(final AjaxRequestTarget target) {

                                Tab.ALBUM.activateWithState( new AlbumTabPanel.AlbumTabState( getModelObject().getAlbum() ) );
                            }

                            @Override
                            protected String getCaptionString() {

                                return getModelObject().getAlbum().getName();
                            }
                        } );
                    }
                } );
            }}).add( new AjaxFormSubmitBehavior( searchForm, "onsubmit" ) {

            @Override
            protected void onSubmit(final AjaxRequestTarget target) {

                target.addComponent( getForm() );
            }

            @Override
            protected void onError(final AjaxRequestTarget target) {

                // TODO: Feedback.
            }

            @Override
            protected CharSequence getEventHandler() {

                // Prevents the form from generating an http request.
                // If we do not provide this, the AJAX event is processed AND the form still gets submitted.
                // FIXME: Ugly. Should probably be moved into AjaxFormSubmitBehaviour.
                return new AppendingStringBuffer( super.getEventHandler() ).append( "; return false;" );
            }
        } ).setOutputMarkupId( true ) );
    }

    /**
     * <h2>{@link Messages}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> <i>Mar 31, 2010</i> </p>
     *
     * @author lhunath
     */
    public interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link ExpoTabPanel}.
         */
        IModel<String> expoTab();

        /**
         * @return The text to show when a search yields no results.
         */
        String noResults();

        /**
         * @param resultCount The amount of results yielded by the search (should be singular).
         *
         * @return The text to show when a search yields a single result.
         */
        String singularResult(int resultCount);

        /**
         * @param resultCount The amount of results yielded by the search (should be plural).
         *
         * @return The text to show when a search yields multiple results.
         */
        String multipleResults(int resultCount);

        /**
         * @param authenticated <code>true</code>: The current user has authenticated himself.<br> <code>false</code>: The current user has
         *                      not identified himself.
         *
         * @return The text that explains which albums are being shown.
         */
        IModel<String> usersHelp(@BooleanKeyAppender(y = "auth", n = "anon") IModel<Boolean> authenticated);
    }


    /**
     * <h2>{@link ExpoTab}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> [description / usage]. </p>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class ExpoTab implements SnaplogTab<ExpoTabPanel, ExpoTabState> {

        public static final ExpoTab instance = new ExpoTab();

        static final Logger logger = Logger.get( ExpoTab.class );

        /**
         * {@inheritDoc}
         */
        @Override
        public IModel<String> getTitle() {

            return msgs.expoTab();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ExpoTabPanel getPanel(final String panelId) {

            return new ExpoTabPanel( panelId );
        }

        @Override
        public Class<ExpoTabPanel> getPanelClass() {

            return ExpoTabPanel.class;
        }

        @Override
        public ExpoTabState getState(final String fragment) {

            return new ExpoTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isVisible() {

            return true;
        }

        @Override
        public List<? extends SnaplogTool> listTools(final ExpoTabPanel panel) {

            return ImmutableList.of();
        }

        @Override
        public String getTabFragment() {

            return "expo";
        }

        @Override
        public ExpoTabState buildFragmentState(final ExpoTabPanel panel) {

            return new ExpoTabState();
        }

        @Override
        public void applyFragmentState(final ExpoTabPanel panel, final ExpoTabState state)
                throws IncompatibleStateException {

            // No state.
        }
    }


    public static class ExpoTabState extends AbstractFragmentState {

        public ExpoTabState() {

        }

        public ExpoTabState(final String fragment) {

            super( fragment );
        }

        @Override
        protected String getTabFragment() {

            return ExpoTab.instance.getTabFragment();
        }
    }
}
