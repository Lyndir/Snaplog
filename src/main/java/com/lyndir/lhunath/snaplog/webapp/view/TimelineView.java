package com.lyndir.lhunath.snaplog.webapp.view;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.internal.Lists;
import com.lyndir.lhunath.lib.system.localization.UseKey;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


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
// TODO: -View's need their models extracted in -Models
public class TimelineView extends GenericPanel<Album> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    @Inject
    AlbumService albumService;


    /**
     * @param id         The wicket ID of the tab.
     * @param albumModel A model providing the album that the timeline should display media for.
     */
    public TimelineView(String id, IModel<Album> albumModel) {

        super( id, albumModel );
        checkNotNull( albumModel.getObject(), "Model object of TimelineView must not be null" );

        // TODO: Should be smarter about iterating the timeFrames?
        add( new ListView<MediaTimeFrame>( "years", new LoadableDetachableModel<List<MediaTimeFrame>>() {

            @Override
            protected List<MediaTimeFrame> load() {

                if (getModelObject() == null)
                    return null;

                return Lists.newArrayList( albumService.iterateYears( SnaplogSession.get().newToken(), getModelObject() ) );
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


    static interface Messages {

        /**
         * @param numberOfPhotosInYear The amount of photos that exist in that year.
         *
         * @return Text on the year component of the timeline view.
         */
        @UseKey
        String albumTimelineYearPhotos(int numberOfPhotosInYear);
    }
}
