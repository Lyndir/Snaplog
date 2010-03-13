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

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.AlbumProviderType;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.util.LayoutPageUtils;
import com.lyndir.lhunath.snaplog.webapp.provider.UserAlbumsProvider;
import com.lyndir.lhunath.snaplog.webapp.tab.model.GalleryTabModels;
import com.lyndir.lhunath.snaplog.webapp.tab.model.GalleryTabModels.NewAlbumFormModels;
import com.lyndir.lhunath.snaplog.webapp.view.MediaView;


/**
 * <h2>{@link GalleryTabPanel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 1, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class GalleryTabPanel extends GenericPanel<GalleryTabModels> {

    Messages msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    UserService userService;

    @Inject
    AlbumService albumService;


    /**
     * Create a new {@link GalleryTabPanel} instance.
     * 
     * @param id
     *            The wicket ID that will hold the {@link GalleryTabPanel}.
     * @param userModel
     *            The user whose gallery to show.
     */
    public GalleryTabPanel(String id, IModel<User> userModel) {

        super( id, new GalleryTabModels( userModel ).getModel() );
        getModelObject().attach( this );

        add( new Label( "albumsTitleUsername", getModelObject().decoratedUsername() ) );
        add( new Label( "albumsHelpUsername", getModelObject().username() ) );

        add( new DataView<Album>( "albums", new UserAlbumsProvider( userService, getModelObject() ) ) {

            @Override
            protected void populateItem(Item<Album> item) {

                item.add( new AjaxLink<Album>( "link", item.getModel() ) {

                    {
                        List<Media> albumFiles = albumService.getFiles( getModelObject() );
                        add( new MediaView( "cover", new Model<Media>( albumFiles.get( albumFiles.size() - 1 ) ),
                                Quality.THUMBNAIL, false ) );
                        add( new Label( "title", getModelObject().getName() ) );
                        // TODO: Fix HTML injection.
                        add( new Label( "description", getModelObject().getDescription() ).setEscapeModelStrings( false ) );
                    }


                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        SnaplogSession.get().setFocussedAlbum( getModelObject() );
                        LayoutPageUtils.setActiveTab( Tab.ALBUM, target );
                    }
                } );
            }
        } );

        final Form<NewAlbumFormModels> newAlbumForm = new Form<NewAlbumFormModels>( "newAlbumForm",
                getModelObject().newAlbumForm().getModel() ) {

            {
                add( new DropDownChoice<AlbumProviderType>( "type", getModelObject().type(), getModelObject().types() ) );

                add( new TextField<String>( "name", getModelObject().name() ) );
                add( new TextArea<String>( "description", getModelObject().description() ) );
            }
        };
        add( newAlbumForm.setVisible( false ).setOutputMarkupPlaceholderTag( true ) );
        add( new AjaxLink<Object>( "newAlbum" ) {

            @Override
            public void onClick(AjaxRequestTarget target) {

                newAlbumForm.setVisible( !newAlbumForm.isVisible() );

                target.addComponent( newAlbumForm );
            }
        } );
    }
}


/**
 * <h2>{@link GalleryTab}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>May 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
class GalleryTab implements ITab {

    static final Logger logger = Logger.get( GalleryTab.class );
    Messages msgs = LocalizerFactory.getLocalizer( Messages.class );


    /**
     * {@inheritDoc}
     */
    @Override
    public IModel<String> getTitle() {

        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                return msgs.galleryTab();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Panel getPanel(String panelId) {

        return new GalleryTabPanel( panelId, new IModel<User>() {

            @Override
            public void detach() {

            }

            @Override
            public User getObject() {

                return SnaplogSession.get().getFocussedUser();
            }

            @Override
            public void setObject(User object) {

                SnaplogSession.get().setFocussedUser( object );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return SnaplogSession.get().getFocussedUser() != null;
    }
}
