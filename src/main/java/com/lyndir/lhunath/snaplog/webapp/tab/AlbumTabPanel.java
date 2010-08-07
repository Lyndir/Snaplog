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
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.AlbumUnavailableException;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.tab.model.AlbumTabModels;
import com.lyndir.lhunath.snaplog.webapp.tool.*;
import com.lyndir.lhunath.snaplog.webapp.view.BrowserView;
import com.lyndir.lhunath.snaplog.webapp.view.FocusedView;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link AlbumTabPanel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 1, 2010</i> </p>
 *
 * @author lhunath
 */
public class AlbumTabPanel extends GenericPanel<AlbumTabModels> {

    /**
     * Create a new {@link AlbumTabPanel} instance.
     *
     * @param id    The wicket ID that will hold the {@link AlbumTabPanel}.
     * @param model Provides the album to show.
     */
    AlbumTabPanel(final String id, final IModel<Album> model) {

        super( id, new AlbumTabModels( model ).getModel() );

        add( new BrowserView( "browser", model ) {

            @Override
            public boolean isVisible() {

                return super.isVisible() && AlbumTabPanel.this.getModelObject().focusedMedia().getObject() == null;
            }
        } );
        add( new FocusedView( "focused", getModelObject().focusedMedia() ) );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link AlbumTabPanel}.
         */
        IModel<String> albumTab();

        /**
         * @return Text on the "Back to Album" tool.
         */
        IModel<String> back();

        /**
         * @param mediaName The name of the media that couldn't be found.
         * @param album     The album in which we attempted to find the media.
         *
         * @return An error message explaining that a certain media was requested but couldn't be found in a certain album.
         */
        IModel<String> errorMediaNotFound(String mediaName, Album album);
    }


    /**
     * <h2>{@link AlbumTab}<br> <sub>The interface panel for browsing through the album content.</sub></h2>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class AlbumTab implements SnaplogTab<AlbumTabPanel, AlbumTabState> {

        public static final AlbumTab instance = new AlbumTab();

        static final Logger logger = Logger.get( AlbumTab.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

        /**
         * {@inheritDoc}
         */
        @Override
        public IModel<String> getTitle() {

            return msgs.albumTab();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AlbumTabPanel getPanel(final String panelId) {

            return new AlbumTabPanel( panelId, SnaplogSession.getFocusedAlbumProxyModel() );
        }

        @Override
        public Class<AlbumTabPanel> getPanelClass() {

            return AlbumTabPanel.class;
        }

        @Override
        public AlbumTabState getState(final String fragment) {

            return new AlbumTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends SnaplogTool> listTools(final AlbumTabPanel panel) {

            return ImmutableList.of( new BackTool( panel.getModelObject() ), //
                                     new TimelinePopup.Tool( panel.getModelObject() ), //
                                     new TagsPopup.Tool( panel.getModelObject() ), //
                                     new AccessPopup.Tool( panel.getModelObject() ) //
            );
        }

        @Override
        public String getTabFragment() {

            return "album";
        }

        @Override
        public AlbumTabState buildFragmentState(final AlbumTabPanel panel) {

            Media focusedMedia = panel.getModelObject().focusedMedia().getObject();
            if (focusedMedia != null)
                return new AlbumTabState( focusedMedia );

            return new AlbumTabState( SnaplogSession.get().getFocusedAlbum() );
        }

        @Override
        public void applyFragmentState(final AlbumTabPanel panel, final AlbumTabState state)
                throws IncompatibleStateException {

            try {
                logger.dbg( "Activating state: %s, on album tab.", state );
                SnaplogSession.get().setFocusedUser( state.getUser() );
                SnaplogSession.get().setFocusedAlbum( state.getAlbum() );
                panel.getModelObject().focusedMedia().setObject( state.findMedia() );
                logger.dbg( "State is now: focused album=%s, focused media=%s", SnaplogSession.get().getFocusedAlbum(),
                            panel.getModelObject().focusedMedia().getObject() );
            }

            catch (UserNotFoundException e) {
                throw new IncompatibleStateException( e );
            }
            catch (AlbumUnavailableException e) {
                throw new IncompatibleStateException( e );
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isVisible() {

            return SnaplogSession.get().getFocusedAlbum() != null;
        }
    }


    static class BackTool implements SnaplogLinkTool {

        static final Messages msgs = MessagesFactory.create( Messages.class );
        static final Logger logger = Logger.get( BackTool.class );

        private final AlbumTabModels model;

        BackTool(final AlbumTabModels model) {

            this.model = model;
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {

            // TODO: Figure out why state change isn't triggering tabContainer and contentContainer to get updated on page.
            model.focusedMedia().setObject( null );
        }

        @Override
        public IModel<String> getTitle() {

            return msgs.back();
        }

        @Override
        public IModel<String> getTitleClass() {

            return new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {

                    return "ss_sprite ss_application_view_tile";
                }
            };
        }

        @Override
        public boolean isVisible() {

            return model.focusedMedia().getObject() != null && model.getObject() != null && GuiceContext.getInstance(
                    SecurityService.class ).hasAccess( Permission.VIEW, SnaplogSession.get().newToken(), model.getObject() );
        }
    }


    public static class AlbumTabState extends AbstractFragmentState {

        static final Logger logger = Logger.get( AlbumTabState.class );

        private final UserService userService = GuiceContext.getInstance( UserService.class );
        private final AlbumService albumService = GuiceContext.getInstance( AlbumService.class );

        final String userName;
        final String albumName;
        final String mediaName;

        public AlbumTabState() {

            userName = null;
            albumName = null;
            mediaName = null;
        }

        public AlbumTabState(final String fragment) {

            super( fragment );

            // Load fields from fragments.
            userName = findFragment( 1 );
            albumName = findFragment( 2 );
            mediaName = findFragment( 3 );
        }

        public AlbumTabState(final Album album) {

            checkNotNull( album, "Album can't be null when creating state based on it." );

            // Load fields and fragments from parameter.
            appendFragment( userName = album.getOwner().getUserName() );
            appendFragment( albumName = album.getName() );
            appendFragment( mediaName = null );
        }

        public AlbumTabState(final Media media) {

            checkNotNull( media, "Media can't be null when creating state based on it." );

            // Load fields and fragments from parameter.
            appendFragment( userName = media.getAlbum().getOwner().getUserName() );
            appendFragment( albumName = media.getAlbum().getName() );
            appendFragment( mediaName = media.getName() );
        }

        public User getUser()
                throws UserNotFoundException {

            return userService.getUserWithUserName( checkNotNull( userName, "Username must not be null in this state." ) );
        }

        public Album getAlbum()
                throws AlbumUnavailableException, UserNotFoundException {

            User user = getUser();
            Album album = albumService.findAlbumWithName( SnaplogSession.get().newToken(), user, albumName );
            if (album == null)
                throw new AlbumUnavailableException( user, albumName );

            return album;
        }

        public Media findMedia()
                throws AlbumUnavailableException, UserNotFoundException {

            return mediaName == null? null: albumService.findMediaWithName( SnaplogSession.get().newToken(), getAlbum(), mediaName );
        }

        @Override
        protected String getTabFragment() {

            return AlbumTab.instance.getTabFragment();
        }
    }
}
