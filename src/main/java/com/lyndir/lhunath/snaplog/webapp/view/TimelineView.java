package com.lyndir.lhunath.snaplog.webapp.view;

import java.text.MessageFormat;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;


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
public class TimelineView extends GenericPanel<Album> {

    final Messages msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService albumService;


    /**
     * @param id
     *            The wicket ID of the tab.
     * @param albumModel
     *            A model providing the album that the timeline should display media for.
     */
    public TimelineView(String id, IModel<Album> albumModel) {

        super( id, albumModel );

        add( new ListView<MediaTimeFrame>( "years", new LoadableDetachableModel<List<MediaTimeFrame>>() {

            @Override
            protected List<MediaTimeFrame> load() {

                if (getModelObject() == null)
                    return null;

                return albumService.getYears( getModelObject() );
            }
        } ) {

            @Override
            public boolean isVisible() {

                return getModelObject() != null;
            }

            @Override
            protected void populateItem(ListItem<MediaTimeFrame> yearItem) {

                MediaTimeFrame mediaYear = yearItem.getModelObject();

                yearItem.add( new Label( "name", Integer.toString( mediaYear.getTime().getYear() ) ) );
                yearItem.add( new Label( "photos", msgs.albumTimelineYearPhotos( mediaYear.getFiles( true ).size() ) ) );

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

                            private IModel<String> daysStyle(final ListItem<MediaTimeFrame> dayItem) {

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
