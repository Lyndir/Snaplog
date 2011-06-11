package com.lyndir.lhunath.snaplog.webapp.tool;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.util.List;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;


/**
 * <h2>{@link TimelinePopup}<br> <sub>Popup that allows user to browse through media on a timeline.</sub></h2>
 *
 * <p> <i>Jan 4, 2010</i> </p>
 *
 * @author lhunath
 */
public class TimelinePopup extends PopupPanel<Tag> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    /**
     * @param id         The wicket ID of the tab.
     * @param tagModel A model providing the tag that the timeline should display media for.
     */
    public TimelinePopup(final String id, final IModel<Tag> tagModel) {

        super( id, tagModel );
    }

    @Override
    protected void initContent(final WebMarkupContainer content) {

        // TODO: Should be smarter about iterating the timeFrames?
        content.add( new ListView<TimeFrame>( "years", new LoadableDetachableModel<List<TimeFrame>>() {

            @Override
            protected List<TimeFrame> load() {

                if (getModelObject() == null)
                    return ImmutableList.of();

                return null;// FIXME: Lists.newArrayList( tagService.iterateYears( SnaplogSession.get().newToken(), getModelObject() ) );
            }
        } ) {

            @Override
            public boolean isVisible() {

                return getModelObject() != null;
            }

            @Override
            protected void populateItem(final ListItem<TimeFrame> yearItem) {

                TimeFrame year = yearItem.getModelObject();

                //                yearItem.add( new Label( "name", Integer.toString( year.getTime().getYear() ) ) );
                //                yearItem.add( new Label( "photos", msgs.yearPhotos( year.getFiles( true ).size() ) ) );

                // Hide the months in the year initially.
                //                yearItem.add( new ListView<TimeFrame>( "months", ImmutableList.copyOf( year ) ) {
                //
                //                    @Override
                //                    protected void populateItem(final ListItem<TimeFrame> monthItem) {
                //
                //                        TimeFrame mediaMonth = monthItem.getModelObject();
                //
                //                        monthItem.add( new Label( "name", mediaMonth.getShortName() ) );
                //                        monthItem.add( new ListView<TimeFrame>( "days", ImmutableList.copyOf( mediaMonth ) ) {
                //
                //                            @Override
                //                            protected void populateItem(final ListItem<TimeFrame> dayItem) {
                //
                //                                TimeFrame mediaDay = dayItem.getModelObject();
                //                                dayItem.add( new AttributeAppender( "style", daysStyle( dayItem ), ";" ) );
                //                                dayItem.add( new Label( "name", mediaDay.getShortName() ) );
                //                            }
                //
                //                            private IModel<String> daysStyle(final ListItem<TimeFrame> dayItem) {
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

    @Override
    public boolean isVisible() {

        return new Tool( getModel() ).isVisible();
    }

    interface Messages {

        /**
         * @param numberOfPhotosInYear The amount of photos that exist in that year.
         *
         * @return Text on the year component of the timeline view.
         */
        IModel<String> yearPhotos(int numberOfPhotosInYear);

        /**
         * @return The title of the toolbar tool that will activate the Timeline popup.
         */
        IModel<String> tool();
    }


    public static class Tool implements SnaplogPanelTool {

        private final IModel<Tag> tagModel;

        /**
         * @param tagModel The model that provides the tag whose access should be managed through this tool.
         */
        public Tool(final IModel<Tag> tagModel) {

            this.tagModel = tagModel;
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

            return new TimelinePopup( id, tagModel );
        }

        @Override
        public boolean isVisible() {

            return tagModel.getObject() != null && GuiceContext.getInstance( SecurityService.class )
                    .hasAccess( Permission.VIEW, SnaplogSession.get().newToken(), tagModel.getObject() );
        }
    }
}
