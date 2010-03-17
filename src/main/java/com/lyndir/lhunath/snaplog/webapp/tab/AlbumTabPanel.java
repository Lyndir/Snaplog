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

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tab.model.AlbumTabModels;
import com.lyndir.lhunath.snaplog.webapp.view.AccessView;
import com.lyndir.lhunath.snaplog.webapp.view.BrowserView;
import com.lyndir.lhunath.snaplog.webapp.view.TagsView;
import com.lyndir.lhunath.snaplog.webapp.view.TimelineView;


/**
 * <h2>{@link AlbumTabPanel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 1, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class AlbumTabPanel extends GenericPanel<AlbumTabModels> {

    /**
     * Create a new {@link AlbumTabPanel} instance.
     * 
     * @param id
     *            The wicket ID that will hold the {@link AlbumTabPanel}.
     * @param model
     *            Provides the album to show.
     */
    public AlbumTabPanel(String id, IModel<Album> model) {

        super( id, new AlbumTabModels( model ).getModel() );

        // Browser
        add( new BrowserView( "browser", getModelObject(), getModelObject().currentTime() ) );

        // Timeline.
        add( new TimelineView( "timelinePopup", getModelObject() ) );

        // Tags.
        add( new TagsView( "tagsPopup", getModelObject() ) );

        // Access.
        add( new AccessView( "accessPopup", getModelObject() ) );
    }
}


/**
 * <h2>{@link AlbumTab}<br>
 * <sub>The interface panel for browsing through the album content.</sub></h2>
 * 
 * <p>
 * <i>May 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
class AlbumTab implements ITab {

    static final Logger logger = Logger.get( AlbumTab.class );
    Messages msgs = LocalizerFactory.getLocalizer( Messages.class );


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
    public Panel getPanel(String panelId) {

        return new AlbumTabPanel( panelId, new IModel<Album>() {

            @Override
            public void detach() {

            }

            @Override
            public Album getObject() {

                return SnaplogSession.get().getFocussedAlbum();
            }

            @Override
            public void setObject(Album object) {

                SnaplogSession.get().setFocussedAlbum( object );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return SnaplogSession.get().getFocussedAlbum() != null;
    }
}