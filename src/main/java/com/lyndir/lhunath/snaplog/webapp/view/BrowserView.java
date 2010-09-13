package com.lyndir.lhunath.snaplog.webapp.view;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.AjaxLabelLink;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.provider.AbstractCollectionProvider;
import com.lyndir.lhunath.lib.wayward.provider.AbstractIteratorProvider;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tab.AlbumTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import java.util.Collection;
import java.util.Iterator;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.joda.time.DateTimeFieldType;


/**
 * <h2>{@link BrowserView}<br> <sub>Component that allows users to view media and browse to similar media.</sub></h2>
 *
 * <p> <i>Jan 6, 2010</i> </p>
 *
 * @author lhunath
 */
public class BrowserView extends GenericPanel<Album> {

    static final Logger logger = Logger.get( BrowserView.class );

    @Inject
    AlbumService albumService;

    public BrowserView(final String id, final IModel<Album> albumModel) {

        super( id, albumModel );
        setOutputMarkupId( true );

        add( new DataView<MediaTimeFrame>( "years", new AbstractIteratorProvider<MediaTimeFrame>() {

            @Override
            protected Iterator<MediaTimeFrame> load() {

                return albumService.iterateMediaTimeFrames( SnaplogSession.get().newToken(), getModelObject(), DateTimeFieldType.year(),
                                                            false );
            }

            @Override
            public int size() {

                return Integer.MAX_VALUE;
            }
        } ) {

            @Override
            protected void populateItem(final Item<MediaTimeFrame> yearItem) {

                yearItem.add( new Label( "name", new LoadableDetachableModel<String>() {

                    @Override
                    protected String load() {

                        return yearItem.getModelObject().objectDescription();
                    }
                } ) );
                yearItem.add( new DataView<MediaTimeFrame>( "months", new AbstractIteratorProvider<MediaTimeFrame>() {

                    @Override
                    protected Iterator<MediaTimeFrame> load() {

                        return albumService.iterateMediaTimeFrames( SnaplogSession.get().newToken(),
                                                                    yearItem.getModelObject().getMedia().iterator(),
                                                                    DateTimeFieldType.monthOfYear() );
                    }

                    @Override
                    public int size() {

                        return yearItem.getModelObject().getMedia().size();
                    }
                } ) {

                    @Override
                    protected void populateItem(final Item<MediaTimeFrame> mediaTimeFrameItem) {

                        mediaTimeFrameItem.setOutputMarkupId( true );
                        final MediaTimeFrame frame = mediaTimeFrameItem.getModelObject();
                        final Component mediaList = new WebMarkupContainer( "mediaList" ) {

                            {
                                add( new DataView<Media>( "media", new AbstractCollectionProvider<Media>() {

                                    @Override
                                    protected Collection<Media> loadSource() {

                                        return frame.getMedia();
                                    }
                                } ) {

                                    @Override
                                    protected void populateItem(final Item<Media> mediaItem) {

                                        mediaItem.add( new MediaView( "media", mediaItem.getModel(), Media.Quality.THUMBNAIL, true ) {

                                            @Override
                                            protected void onClick(@SuppressWarnings("unused") final AjaxRequestTarget target) {

                                                Tab.ALBUM.activateWithState( new AlbumTabPanel.AlbumTabState( getModelObject() ) );
                                            }
                                        } );
                                    }
                                } );
                            }
                        }.setVisible( false );
                        mediaTimeFrameItem.add( mediaList, new AjaxLabelLink( "name", new LoadableDetachableModel<String>() {

                            @Override
                            protected String load() {

                                return frame.objectDescription();
                            }
                        } ) {

                            @Override
                            public void onClick(final AjaxRequestTarget target) {

                                mediaList.setVisible( !mediaList.isVisible() );
                                logger.dbg( "month clicked; list now visible? %s", mediaList.isVisible() );
                                target.addComponent( mediaTimeFrameItem );
                            }
                        } );
                    }
                } );
            }
        } );
    }

    @Override
    public boolean isVisible() {

        return super.isVisible() && getModelObject() != null;
    }
}
