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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.webapp.servlet.ImageServlet;


/**
 * <h2>{@link MediaView}<br>
 * <sub>A view that renders media at a certain quality.</sub></h2>
 * 
 * <p>
 * <i>Mar 13, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class MediaView extends GenericPanel<Media> {

    static final Logger logger = Logger.get( MediaView.class );


    /**
     * @param id
     *            The wicket ID of this component.
     * @param model
     *            The model that will provide the {@link Media} to show in this view.
     * @param quality
     *            The quality at which to show the {@link Media}.
     * @param clickable
     *            <code>true</code>: The media will be clickable. When clicked, the {@link #onClick(AjaxRequestTarget)}
     *            will be fired.<br>
     *            <code>false</code>: The media will not be clickable. There is no need to implement
     *            {@link #onClick(AjaxRequestTarget)}.
     */
    public MediaView(String id, IModel<Media> model, Quality quality, boolean clickable) {

        super( id, model );

        // The media container.
        WebMarkupContainer media = new WebMarkupContainer( "media" );
        add( media.add( new AttributeAppender( "class", new Model<String>( quality.getName() ), " " ) ) );

        // The media image link/container.
        WebMarkupContainer image = null;
        if (clickable) {
            image = new AjaxFallbackLink<String>( "image" ) {

                @Override
                public void onClick(AjaxRequestTarget target) {

                    MediaView.this.onClick( target );
                }
            };
            image.add( new CSSClassAttributeAppender( "link" ) );
        } else
            image = new WebMarkupContainer( "image" );
        image.add( new Label( "caption", getModelObject().getDateString() ) );
        image.add( new ContextImage( "thumb", ImageServlet.getContextRelativePathFor( getModelObject(),
                                                                                      Quality.THUMBNAIL ) ) );
        image.add( new ContextImage( "photo", ImageServlet.getContextRelativePathFor( getModelObject(), quality ) ) );

        // Add the image and the fullscreen image to the media container.
        media.add( image );
        media.add( new ContextImage( "fullImage", //
                ImageServlet.getContextRelativePathFor( getModelObject(), Quality.FULLSCREEN ) ).setVisible( quality == Quality.PREVIEW ) );
    }

    /**
     * Fired when the user clicks the media.
     * 
     * <p>
     * You only need to implement this method when you're actually creating a clickable {@link MediaView}. See the
     * <code>clickable</code> argument of {@link #MediaView(String, IModel, Quality, boolean)}.
     * </p>
     * 
     * @param target
     *            The AJAX request that fired fired this event.
     */
    protected void onClick(@SuppressWarnings("unused") AjaxRequestTarget target) {

        throw new UnsupportedOperationException();
    }
}
