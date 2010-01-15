package com.lyndir.lhunath.snaplog.webapp.components;

import java.text.MessageFormat;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;


/**
 * <h2>{@link TimelineView}<br>
 * <sub>A timeline popup.</sub></h2>
 * 
 * <p>
 * <i>Jan 4, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class TimelineView extends Panel {

    static Messages msgs = LocalizerFactory.getLocalizer( Messages.class );

    @Inject
    AlbumService    albumService;


    public TimelineView(String id) {

        super( id );

        add( new ListView<MediaTimeFrame>( "years", new AbstractReadOnlyModel<List<MediaTimeFrame>>() {

            @Override
            public List<MediaTimeFrame> getObject() {

                return albumService.getYears( SnaplogConstants.DEFAULT_ALBUM );
            }
        } ) {

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
    }
}