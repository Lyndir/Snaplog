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
import com.lyndir.lhunath.lib.system.localization.UseKey;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tab.model.AlbumTabModels;
import com.lyndir.lhunath.snaplog.webapp.tool.AccessPopup;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import com.lyndir.lhunath.snaplog.webapp.view.BrowserView;
import com.lyndir.lhunath.snaplog.webapp.view.TagsView;
import com.lyndir.lhunath.snaplog.webapp.view.TimelineView;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;
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
        checkNotNull( model.getObject(), "Model object of AlbumTabPanel must not be null" );

        // Browser
        add( new BrowserView( "browser", getModelObject(), getModelObject().currentTime() ) );

        // TODO: these panels should come from the SnaplogTool
        // Time line.
        add( new TimelineView( "timelinePopup", getModelObject() ) );

        // Tags.
        add( new TagsView( "tagsPopup", getModelObject() ) );

        // Access.
        add( new AccessPopup( "accessPopup", getModelObject() ) );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link AlbumTabPanel}.
         */
        @UseKey
        String albumTab();
    }


    /**
     * <h2>{@link AlbumTab}<br> <sub>The interface panel for browsing through the album content.</sub></h2>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class AlbumTab implements SnaplogTab {

        static final Logger logger = Logger.get( AlbumTab.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

        private final IModel<Album> model = new IModel<Album>() {

            @Override
            public void detach() {

            }

            @Override
            public Album getObject() {

                return SnaplogSession.get().getFocusedAlbum();
            }

            @Override
            public void setObject(final Album object) {

                SnaplogSession.get().setFocusedAlbum( object );
            }
        };

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
        public Panel getPanel(final String panelId) {

            return new AlbumTabPanel( panelId, model );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends SnaplogTool> listTools() {

            return ImmutableList.of( new AccessPopup.AccessTool( "accessPopup", model ) );
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
