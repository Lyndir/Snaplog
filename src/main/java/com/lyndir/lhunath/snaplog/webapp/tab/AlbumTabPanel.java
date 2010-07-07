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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ArrayUtils;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.tab.model.AlbumTabModels;
import com.lyndir.lhunath.snaplog.webapp.tool.AccessPopup;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import com.lyndir.lhunath.snaplog.webapp.tool.TagsPopup;
import com.lyndir.lhunath.snaplog.webapp.tool.TimelinePopup;
import com.lyndir.lhunath.snaplog.webapp.view.BrowserView;
import com.lyndir.lhunath.snaplog.webapp.view.FocusedView;
import java.util.List;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


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

        add( new BrowserView( "browser", model ) );
        add( new FocusedView( "focused", getModelObject().focusedMedia() ) );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link AlbumTabPanel}.
         */
        String albumTab();

        /**
         * @param mediaName The name of the media that couldn't be found.
         * @param album     The album in which we attempted to find the media.
         *
         * @return An error message explaining that a certain media was requested but couldn't be found in a certain album.
         */
        String errorMediaNotFound(String mediaName, Album album);
    }


    /**
     * <h2>{@link AlbumTab}<br> <sub>The interface panel for browsing through the album content.</sub></h2>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class AlbumTab implements SnaplogTab<AlbumTabPanel> {

        static final Logger logger = Logger.get( AlbumTab.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

        /**
         * {@inheritDoc}
         */
        @Override
        public IModel<String> getTitle() {

            return new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    return msgs.albumTab();
                }
            };
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

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends SnaplogTool> listTools() {

            IModel<Album> model = SnaplogSession.getFocusedAlbumProxyModel();
            return ImmutableList.of( new TimelinePopup.Tool( model ), new TagsPopup.Tool( model ), new AccessPopup.Tool( model ) );
        }

        @Override
        public String getFragment() {

            return "album";
        }

        @Override
        public Iterable<String> getFragmentState(final AlbumTabPanel panel) {

            checkNotNull( panel, "Panel must not be null." );
            Preconditions.checkArgument( AlbumTabPanel.class.isInstance( panel ), "Panel must be an %s.", AlbumTabPanel.class );
            AlbumTabPanel albumPanel = (AlbumTabPanel) panel;

            Media focusedMedia = albumPanel.getModelObject().focusedMedia().getObject();
            if (focusedMedia == null)
                return ImmutableList.of( SnaplogSession.get().getFocusedUser().getUserName(), // 1
                                         SnaplogSession.get().getFocusedAlbum().getName() // 2
                );
            else
                return ImmutableList.of( SnaplogSession.get().getFocusedUser().getUserName(), // 1
                                         SnaplogSession.get().getFocusedAlbum().getName(), // 2
                                         focusedMedia.getName() // 3
                );
        }

        @Override
        public void applyFragmentState(final AlbumTabPanel panel, final String... arguments) {

            checkNotNull( panel, "Panel must not be null." );
            Preconditions.checkArgument( AlbumTabPanel.class.isInstance( panel ), "Panel must be an %s.", AlbumTabPanel.class );
            AlbumTabPanel albumPanel = (AlbumTabPanel) panel;

            if (ArrayUtils.hasIndex( 1, arguments )) {
                String userName = arguments[1];

                User user = GuiceContext.get().getInstance( UserService.class ).findUserWithUserName( userName );
                SnaplogSession.get().setFocusedUser( user );
            }
            if (ArrayUtils.hasIndex( 2, arguments ) && SnaplogSession.get().getFocusedUser() != null) {
                String albumName = arguments[2];

                Album album = GuiceContext.get()
                        .getInstance( AlbumService.class )
                        .findAlbumWithName( SnaplogSession.get().newToken(), SnaplogSession.get().getFocusedUser(), albumName );
                SnaplogSession.get().setFocusedAlbum( album );
            }
            Album focusedAlbum = SnaplogSession.get().getFocusedAlbum();
            if (ArrayUtils.hasIndex( 3, arguments ) && focusedAlbum != null) {
                String mediaName = arguments[3];

                Media media = GuiceContext.get()
                        .getInstance( AlbumService.class )
                        .findMediaWithName( SnaplogSession.get().newToken(), focusedAlbum, mediaName );

                if (media == null)
                    albumPanel.error( msgs.errorMediaNotFound( mediaName, focusedAlbum ) );

                albumPanel.getModelObject().focusedMedia().setObject( media );
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
}
