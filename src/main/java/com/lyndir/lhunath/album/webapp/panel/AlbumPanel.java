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
package com.lyndir.lhunath.album.webapp.panel;

import java.text.MessageFormat;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.lyndir.lhunath.album.messages.Messages;
import com.lyndir.lhunath.album.model.MediaService;
import com.lyndir.lhunath.album.model.MediaTimeFrame;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;


/**
 * <h2>{@link AlbumPanel}<br>
 * <sub>The interface panel for browsing through the album content.</sub></h2>
 * 
 * <p>
 * <i>May 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class AlbumPanel extends Panel {

    static Messages msgs = LocalizerFactory.getLocalizer( Messages.class );


    /**
     * @param id
     *            The Wicket ID of this panel.
     */
    public AlbumPanel(String id) {

        super( id );

        add( new Label( "title", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                // TODO: Unhardcode username.
                return msgs.albumTitle( "~", "lhunath" );
            }
        } ) );

        add( new ListView<MediaTimeFrame>( "years", MediaService.getTimeFrames() ) {

            @Override
            protected void populateItem(final ListItem<MediaTimeFrame> yearItem) {

                final MediaTimeFrame mediaYear = yearItem.getModelObject();

                yearItem.add( new Label( "name", Integer.toString( mediaYear.getTime().getYear() ) ) );
                yearItem.add( new Label( "photos", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {

                        return msgs.albumTimelineYearPhotos( mediaYear.getFiles( true ).size() );
                    }
                } ) );

                // Hide the months in the year initially.
                yearItem.add( new ListView<MediaTimeFrame>( "months", mediaYear ) {

                    @Override
                    protected void populateItem(ListItem<MediaTimeFrame> monthItem) {

                        MediaTimeFrame mediaMonth = monthItem.getModelObject();

                        monthItem.add( new Label( "name", mediaMonth.getShortName() ) );
                        monthItem.add( new ListView<MediaTimeFrame>( "days", mediaMonth ) {

                            @Override
                            protected void populateItem(ListItem<MediaTimeFrame> dayItem) {

                                MediaTimeFrame mediaDay = dayItem.getModelObject();
                                dayItem.add( new AttributeAppender( "style", daysStyle( dayItem ), ";" ) );
                                dayItem.add( new Label( "name", mediaDay.getShortName() ) );
                            }

                            private IModel<?> daysStyle(final ListItem<MediaTimeFrame> dayItem) {

                                return new AbstractReadOnlyModel<String>() {

                                    @Override
                                    public String getObject() {

                                        int photos = dayItem.getModelObject().getFiles( true ).size();
                                        double height = photos == 0? 0: Math.log10( photos ) * 10;
                                        return MessageFormat.format( "border-width: {0}px", height );
                                    }
                                };
                            }
                        } );
                    }
                } );
            }
        } );

        add( new WebMarkupContainer( "photo" ) );
    }
}
