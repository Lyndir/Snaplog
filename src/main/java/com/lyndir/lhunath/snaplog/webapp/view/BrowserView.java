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
import java.util.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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

        add( new MediaView( "media", new LoadableDetachableModel<Media>() {
            @Override
            protected Media load() {

                resetMediaToCurrent();
                return mediaView.current();
            }
        }, Media.Quality.FULLSCREEN, false ) );
        add( new RefreshingView<Media>( "before" ) {

            @Override
            protected Iterator<IModel<Media>> getItemModels() {

                resetMediaToCurrent();
                Media current = mediaView.current();

                List<IModel<Media>> models = new LinkedList<IModel<Media>>();
                // Go back 6 (or less)
                for (int i = 0; i < 10; ++i)
                    if (mediaView.hasPrevious())
                        mediaView.previous();

                // Add 6 or up to the current or last media to the models list.
                for (int i = 0; i < 10; ++i) {
                    if (mediaView.current().equals( current ))
                        break;
                    if (!mediaView.hasNext())
                        break;

                    models.add( new Model<Media>( mediaView.current() ) );
                    mediaView.next();
                }

                // Return an iterator limited at 'count'.
                return models.iterator();
            }

            @Override
            protected void populateItem(final Item<Media> item) {

                item.add( new MediaView( "media", item.getModel(), Media.Quality.THUMBNAIL, true ) {
                    @Override
                    protected void onClick(final AjaxRequestTarget target) {

                        currentTimeModel.setObject( new Date( item.getModelObject().shotTime() ) );
                        target.addComponent( BrowserView.this );
                    }
                } );
            }
        } );
        add( new RefreshingView<Media>( "after" ) {

            @Override
            protected Iterator<IModel<Media>> getItemModels() {

                resetMediaToCurrent();

                List<IModel<Media>> models = new LinkedList<IModel<Media>>();
                // Add 6 or up to the last media to the models list.
                for (int i = 0; i < 10; ++i) {
                    if (mediaView.hasNext())
                        models.add( new Model<Media>( mediaView.next() ) );
                }

                // Return an iterator limited at 'count'.
                return models.iterator();
            }

            @Override
            protected void populateItem(final Item<Media> item) {

                item.add( new MediaView( "media", item.getModel(), Media.Quality.THUMBNAIL, true ) {
                    @Override
                    protected void onClick(final AjaxRequestTarget target) {

                        currentTimeModel.setObject( new Date( item.getModelObject().shotTime() ) );
                        target.addComponent( BrowserView.this );
                    }
                } );
            }
        } );
    }

    void resetMediaToCurrent() {

        // Find current media in mediaView
        if (currentTimeModel.getObject() == null) {
            // No time set, fast-forward to the last one.
            if (mediaView.hasNext())
                Iterators.getLast( mediaView ); // cursor to after last.
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
    }
}
