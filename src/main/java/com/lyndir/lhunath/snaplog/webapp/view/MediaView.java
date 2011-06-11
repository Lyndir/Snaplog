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
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.opal.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.model.service.impl.SourceDelegate;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tab.SharedTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import java.net.URL;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.*;


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
    SourceDelegate sourceDelegate;

    @Inject
    SecurityService securityService;

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
        media.add( image );
        media.add( new WebMarkupContainer( "tools" ) {

            {
                add( new ExternalLink( "original", new LoadableDetachableModel<String>() {

                    @Override
                    protected String load() {

                        try {
                            URL resourceURL = sourceDelegate.findResourceURL( SnaplogSession.get().newToken(), getModelObject(),
                                                                            Quality.ORIGINAL );
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
                    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

                        if (!renderAsMini( markupStream, openTag ))
                            super.onComponentTagBody( markupStream, openTag );
                    }
                } );
                add( new AjaxLink<Media>( "share", getModel() ) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {

                        try {
                            Tab.SHARED.activateWithState( new SharedTabPanel.SharedTabState( getModelObject() ) );
                        }
                        catch (PermissionDeniedException e) {
                            error( e.getLocalizedMessage() );
                        }
                    }

                    @Override
                    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

                        if (!renderAsMini( markupStream, openTag ))
                            super.onComponentTagBody( markupStream, openTag );
                    }

                    @Override
                    public boolean isVisible() {

                        return securityService.hasAccess( Permission.ADMINISTER, SnaplogSession.get().newToken(), getModelObject() );
                    }
                } );
                add( new WebMarkupContainer( "permissions" ) {
                    @Override
                    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

                        if (!renderAsMini( markupStream, openTag ))
                            super.onComponentTagBody( markupStream, openTag );
                    }

                    @Override
                    public boolean isVisible() {

                        return securityService.hasAccess( Permission.ADMINISTER, SnaplogSession.get().newToken(), getModelObject() );
                    }
                } );
                add( new Link<Media>( "delete", getModel() ) {
                    @Override
                    public void onClick() {

                        try {
                            sourceDelegate.delete( SnaplogSession.get().newToken(), getModelObject() );
                        }
                        catch (PermissionDeniedException e) {
                            error( e.getLocalizedMessage() );
                        }
                    }

                    @Override
                    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

                        if (!renderAsMini( markupStream, openTag ))
                            super.onComponentTagBody( markupStream, openTag );
                    }

                    @Override
                    public boolean isVisible() {

                        return securityService.hasAccess( Permission.ADMINISTER, SnaplogSession.get().newToken(), getModelObject() );
                    }
                } );

                add( CSSClassAttributeAppender.of( new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {

                        return isMini( quality )? "mini": null;
                    }
                } ) );
            }

            @Override
            public boolean isVisible() {

                return getModelObject() != null;
            }

            private boolean renderAsMini(final MarkupStream markupStream, final ComponentTag openTag) {

                if (isMini( quality )) {
                    // Discard all elements from the markup stream until our close tag.
                    while (markupStream.hasMore())
                        if (markupStream.next().closes( openTag ))
                            break;

                    replaceComponentTagBody( markupStream, openTag, "" );
                    return true;
                }

                return false;
            }
        } );

        image.add( new AttributeModifier( "style", true, new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                try {
                    URL resourceURL = sourceDelegate.findResourceURL( SnaplogSession.get().newToken(), getModelObject(), quality );
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
        image.add( new ContextImage( "thumb", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                logger.dbg( "Loading Media: %s, Quality: %s", getModelObject(), quality );

                try {
                    URL resourceURL = sourceDelegate.findResourceURL( SnaplogSession.get().newToken(), getModelObject(), Quality.THUMBNAIL );
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
        image.add( new ContextImage( "full", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                try {
                    URL resourceURL = sourceDelegate.findResourceURL( SnaplogSession.get().newToken(), getModelObject(), quality );
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

                return quality == Quality.FULLSCREEN;
            }
        } );
        image.add( new Label( "caption", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return getCaptionString();
            }
        } ) {

            @Override
            public boolean isVisible() {

                return getModelObject() != null;
            }
        } );
    }

    private static boolean isMini(final Quality quality) {

        return quality == Quality.THUMBNAIL;
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
