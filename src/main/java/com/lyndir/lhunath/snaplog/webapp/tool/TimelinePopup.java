package com.lyndir.lhunath.snaplog.webapp.tool;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.util.List;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link TimelinePopup}<br> <sub>Popup that allows user to browse through media on a timeline.</sub></h2>
 *
 * <p> <i>Jan 4, 2010</i> </p>
 *
 * @author lhunath
 */
public class TimelinePopup extends PopupPanel<Album> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    @Inject
    AlbumService albumService;

    /**
     * @param id         The wicket ID of the tab.
     * @param albumModel A model providing the album that the timeline should display media for.
     */
    public TimelinePopup(final String id, final IModel<Album> albumModel) {

        super( id, albumModel );
        checkNotNull( albumModel.getObject(), "Model object of TimelinePopup must not be null" );
    }

    @Override
    protected void initContent(final WebMarkupContainer content) {

        // TODO: Should be smarter about iterating the timeFrames?
        content.add( new ListView<MediaTimeFrame>( "years", new LoadableDetachableModel<List<MediaTimeFrame>>() {

            @Override
            protected List<MediaTimeFrame> load() {

                if (getModelObject() == null)
                    return ImmutableList.of();

                return null;// FIXME: Lists.newArrayList( albumService.iterateYears( SnaplogSession.get().newToken(), getModelObject() ) );
            }
        } ) {

            @Override
            public boolean isVisible() {

                return getModelObject() != null;
            }

            @Override
            protected void populateItem(final ListItem<MediaTimeFrame> yearItem) {

                MediaTimeFrame mediaYear = yearItem.getModelObject();

                //                yearItem.add( new Label( "name", Integer.toString( mediaYear.getTime().getYear() ) ) );
                //                yearItem.add( new Label( "photos", msgs.yearPhotos( mediaYear.getFiles( true ).size() ) ) );

                // Hide the months in the year initially.
                //                yearItem.add( new ListView<MediaTimeFrame>( "months", ImmutableList.copyOf( mediaYear ) ) {
                //
                //                    @Override
                //                    protected void populateItem(final ListItem<MediaTimeFrame> monthItem) {
                //
                //                        MediaTimeFrame mediaMonth = monthItem.getModelObject();
                //
                //                        monthItem.add( new Label( "name", mediaMonth.getShortName() ) );
                //                        monthItem.add( new ListView<MediaTimeFrame>( "days", ImmutableList.copyOf( mediaMonth ) ) {
                //
                //                            @Override
                //                            protected void populateItem(final ListItem<MediaTimeFrame> dayItem) {
                //
                //                                MediaTimeFrame mediaDay = dayItem.getModelObject();
                //                                dayItem.add( new AttributeAppender( "style", daysStyle( dayItem ), ";" ) );
                //                                dayItem.add( new Label( "name", mediaDay.getShortName() ) );
                //                            }
                //
                //                            private IModel<String> daysStyle(final ListItem<MediaTimeFrame> dayItem) {
                //
                //                                return new AbstractReadOnlyModel<String>() {
                //
                //                                    @Override
                //                                    public String getObject() {
                //
                //                                        int photos = dayItem.getModelObject().getFiles( true ).size();
                //                                        double height = photos == 0? 0: Math.log10( photos ) * 10;
                //                                        return MessageFormat.format( "border-width: {0}px", height );
                //                                    }
                //                                };
                //                            }
                //                        } );
                //                    }
                //                } );
            }
        } );
    }

    interface Messages {

        /**
         * @param numberOfPhotosInYear The amount of photos that exist in that year.
         *
         * @return Text on the year component of the timeline view.
         */
        String yearPhotos(int numberOfPhotosInYear);

        /**
         * @return The title of the toolbar tool that will activate the Timeline popup.
         */
        IModel<String> tool();
    }


    public static class Tool implements SnaplogPanelTool {

        private final IModel<Album> model;

        /**
         * @param model The model that provides the album whose access should be managed through this tool.
         */
        public Tool(final IModel<Album> model) {

            this.model = model;
        }

        @Override
        public IModel<String> getTitle() {

            return msgs.tool();
        }

        @Override
        public IModel<String> getTitleClass() {

            return new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {

                    return "ss_sprite ss_time";
                }
            };
        }

        @Override
        public Panel getPanel(final String id) {

            return new TimelinePopup( id, model );
        }

        @Override
        public boolean isVisible() {

            return GuiceContext.getInstance( SecurityService.class ).hasAccess( Permission.VIEW, SnaplogSession.get().newToken(), model.getObject() );
        }
    }
}
