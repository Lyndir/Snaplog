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
package com.lyndir.lhunath.snaplog.webapp.view;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import java.net.URL;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link MediaView}<br> <sub>A view that renders media at a certain quality.</sub></h2>
 *
 * <p> <i>Mar 13, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaView extends GenericPanel<Media> {

    static final Logger logger = Logger.get( MediaView.class );

    @Inject
    AlbumService albumService;

    /**
     * @param id        The wicket ID of this component.
     * @param model     The model that will provide the {@link Media} to show in this view.
     * @param quality   The quality at which to show the {@link Media}.
     * @param clickable <code>true</code>: The media will be clickable. When clicked, the {@link #onClick(AjaxRequestTarget)} will be
     *                  fired.<br> <code>false</code>: The media will not be clickable. There is no need to implement {@link
     *                  #onClick(AjaxRequestTarget)}.
     */
    public MediaView(final String id, final IModel<Media> model, final Quality quality, final boolean clickable) {

        super( id, model );

        // The media container.
        WebMarkupContainer media = new WebMarkupContainer( "media" );
        add( media.add( new AttributeAppender( "class", new Model<String>( quality.getName() ), " " ) ) );

        // The media image link/container.
        WebMarkupContainer image;
        if (clickable) {
            image = new AjaxFallbackLink<Media>( "image", getModel() ) {

                @Override
                public void onClick(final AjaxRequestTarget target) {

                    MediaView.this.onClick( target );
                }

                @Override
                public boolean isVisible() {

                    return getModelObject() != null;
                }
            };
            media.add( new CSSClassAttributeAppender( "link" ) );
        } else
            image = new WebMarkupContainer( "image" ) {

                @Override
                public boolean isVisible() {

                    return getModelObject() != null;
                }
            };
        image.add( new Label( "caption", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return getCaptionString();
            }
        } ) {

            @Override
            public boolean isVisible() {

                return getModelObject() != null && false; // TODO: Show caption with appropriate qualities and make pretty.
            }
        } );
        image.add( new ContextImage( "thumb", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                try {
                    URL resourceURL = albumService.findResourceURL( SnaplogSession.get().newToken(), getModelObject(), Quality.THUMBNAIL );
                    if (resourceURL == null)
                        // TODO: May want to display something useful to the user like a specific "not-found" thumbnail.
                        return null;

                    return resourceURL.toExternalForm();
                }
                catch (PermissionDeniedException ignored) {
                    // TODO: May want to display something useful to the user like a specific "denied" thumbnail.
                    return null;
                }
            }
        } ) );
        image.add( new AttributeModifier( "style", true, new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                try {
                    URL resourceURL = albumService.findResourceURL( SnaplogSession.get().newToken(), getModelObject(), quality );
                    if (resourceURL == null)
                        // TODO: May want to display something useful to the user like a specific "not-found" thumbnail.
                        return null;

                    return String.format( "background-image: url('%s')", resourceURL.toExternalForm() );
                }
                catch (PermissionDeniedException ignored) {
                    // TODO: May want to display something useful to the user like a specific "denied" thumbnail.
                    return null;
                }
            }
        } ) );

        // Add the image and the full-screen image to the media container.
        media.add( image );
        media.add( new ContextImage( "fullImage", //
                                     new LoadableDetachableModel<String>() {

                                         @Override
                                         protected String load() {

                                             try {
                                                 URL resourceURL = albumService.findResourceURL( SnaplogSession.get().newToken(),
                                                                                                 getModelObject(), Quality.FULLSCREEN );
                                                 if (resourceURL == null)
                                                     // TODO: May want to display something useful to the user like a specific "not-found" thumbnail.
                                                     return null;

                                                 return resourceURL.toExternalForm();
                                             }
                                             catch (PermissionDeniedException ignored) {
                                                 // TODO: May want to display something useful to the user like a specific "denied" thumbnail.
                                                 return null;
                                             }
                                         }
                                     } ) {

            @Override
            public boolean isVisible() {

                return getModelObject() != null && quality == Quality.PREVIEW;
            }
        } );
    }

    /**
     * @return The string to show in the caption. Use <code>null</code> to hide the caption.
     */
    protected String getCaptionString() {

        return getModelObject().getDateString();
    }

    /**
     * Fired when the user clicks the media.
     *
     * <p> You only need to implement this method when you're actually creating a clickable {@link MediaView}. See the
     * <code>clickable</code> argument of {@link #MediaView(String, IModel, Quality, boolean)}. </p>
     *
     * @param target The AJAX request that fired fired this event.
     */
    protected void onClick(@SuppressWarnings("unused") final AjaxRequestTarget target) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVisible() {

        return super.isVisible() && getModelObject() != null;
    }
}
