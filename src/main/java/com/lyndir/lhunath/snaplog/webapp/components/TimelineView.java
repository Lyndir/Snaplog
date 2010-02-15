package com.lyndir.lhunath.snaplog.webapp.components;

import java.text.MessageFormat;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.LocalDate;


/**
 * <h2>{@link TimelineView}<br>
 * <sub>Popup that allows user to browse through media on a timeline.</sub></h2>
 *
 * <p>
 * <i>Jan 4, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class TimelineView extends Panel {

    final Messages msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService albumService;


    /**
     * {@inheritDoc}
     */
    public TimelineView(String id, IModel<Album> albumModel) {

        super( id, albumModel );

        add( new ListView<MediaTimeFrame>( "years", new AbstractReadOnlyModel<List<MediaTimeFrame>>() {

            @Override
            public List<MediaTimeFrame> getObject() {

                return albumService.getYears( SnaplogConstants.DEFAULT_ALBUM );
            }
        } ) {

            @Override
            protected void populateItem(final ListItem<MediaTimeFrame> yearItem) {

                MediaTimeFrame mediaYear = yearItem.getModelObject();

                LocalDate mediaTime = mediaYear.getTime();

                yearItem.add( new Label( "name", Integer.toString( mediaYear.getTime().getYear() ) ) );
                yearItem.add( new Label( "photos", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {

                        return msgs.albumTimelineYearPhotos( mediaYear.getFiles( true ).size() );
                    }
                } ) );

                // Hide the months in the year initially.
                yearItem.add( new ListView<MediaTimeFrame>( "months", ImmutableList.copyOf( mediaYear ) ) {

                    @Override
                    protected void populateItem(ListItem<MediaTimeFrame> monthItem) {

                        MediaTimeFrame mediaMonth = monthItem.getModelObject();

                        monthItem.add( new Label( "name", mediaMonth.getShortName() ) );
                        monthItem.add( new ListView<MediaTimeFrame>( "days", ImmutableList.copyOf( mediaMonth ) ) {

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
