package com.lyndir.lhunath.snaplog.webapp.view;

import static com.google.common.base.Preconditions.checkNotNull;

import com.db4o.ObjectSet;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.ListIteratorView;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.servlet.ImageServlet;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link BrowserView}<br> <sub>Component that allows users to browse through media chronologically.</sub></h2>
 *
 * <p> <i>Jan 6, 2010</i> </p>
 *
 * @author lhunath
 */
public class BrowserView extends GenericPanel<Album> {

    static final Logger logger = Logger.get( BrowserView.class );

    static final int BROWSER_SIDE_IMAGES = 4;

    @Inject
    AlbumService albumService;

    Media currentFile;
    final IModel<Date> currentTimeModel;
    final ListIteratorView<Media> mediaView;
    final DataView<Media> pager;

    /**
     * Create a new {@link BrowserView} instance.
     *
     * @param id               The wicket ID to put this component in the HTML.
     * @param albumModel       The model contains the {@link Album} that the browser should get its media from.
     * @param currentTimeModel The model contains the {@link Date} upon which the browser should focus. The first image on or past this date
     *                         will be the focused image.
     */
    public BrowserView(final String id, final IModel<Album> albumModel, final IModel<Date> currentTimeModel) {

        super( id, albumModel );
        checkNotNull( albumModel.getObject(), "Model object of BrowserView must not be null" );
        setOutputMarkupId( true );

        this.currentTimeModel = currentTimeModel;

        mediaView = new ListIteratorView<Media>() {

            transient ObjectSet<Media> objectSet;

            private ObjectSet<Media> getObjectSet() {

                if (objectSet == null)
                    objectSet = albumService.queryMedia( SnaplogSession.get().newToken(), albumModel.getObject() );

                return objectSet;
            }

            @Override
            public int size() {

                return getObjectSet().size();
            }

            @Override
            protected ListIterator<Media> load() {

                return getObjectSet().listIterator();
            }
        };

        add( pager = new DataView<Media>( "pager", new IDataProvider<Media>() {
            @Override
            public Iterator<? extends Media> iterator(final int first, final int count) {

                // Adjust the mediaView so that its cursor starts at 'first'.
                if (first == 0)
                    mediaView.reset();

                else {
                    while (mediaView.currentIndex() >= first)
                        mediaView.previous();
                    while (mediaView.currentIndex() < first - 1)
                        mediaView.next();
                }

                // Return an iterator limited at 'count'.
                logger.dbg( "Pager iterating at offset: %d, count: %d", first, count );
                return Iterators.limit( mediaView, count );
            }

            @Override
            public int size() {

                return mediaView.size();
            }

            @Override
            public IModel<Media> model(final Media object) {

                return new Model<Media>( object );
            }

            @Override
            public void detach() {

            }
        }, 3 ) {
            @Override
            protected void populateItem(final Item<Media> item) {

                item.add( new ContextImage( "image",
                                            ImageServlet.getContextRelativePathFor( item.getModelObject(), Media.Quality.FULLSCREEN ) ) );
            }

            @Override
            protected int getViewOffset() {

                // Find current media in mediaView
                if (currentTimeModel.getObject() == null) {
                    // No time set, fast-forward to the before-last one.
                    Iterators.getLast( mediaView ); // cursor to after last.
                    mediaView.previous(); // back to last.
                    mediaView.previous(); // to before-last.
                } else {
                    // Find the one on or just after the currentTime.
                    long currentTime = currentTimeModel.getObject().getTime();
                    while (mediaView.current().shotTime() > currentTime) {
                        mediaView.previous();
                    }
                    while (mediaView.current().shotTime() < currentTime) {
                        mediaView.next();
                    }
                }

                // ViewOffset is the index of the current media - 1 (since we show one before, current, and one after)
                logger.dbg( "MediaView previous previous: %d", mediaView.previousIndex() );
                return Math.max( 0, mediaView.previousIndex() );
            }
        } );
    }
}
