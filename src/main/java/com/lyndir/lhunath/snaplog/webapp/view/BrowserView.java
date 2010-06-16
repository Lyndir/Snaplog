package com.lyndir.lhunath.snaplog.webapp.view;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.ListIteratorView;
import com.lyndir.lhunath.lib.system.collection.SizedListIterator;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
public class BrowserView extends GenericPanel<Media> {

    static final Logger logger = Logger.get( BrowserView.class );

    static final int BROWSER_SIDE_IMAGES = 4;

    @Inject
    AlbumService albumService;

    Media currentFile;
    final ListIteratorView<Media> mediaView;

    /**
     * Create a new {@link BrowserView} instance.
     *
     * @param id         The wicket ID to put this component in the HTML.
     * @param mediaModel The model contains the {@link Media} that the browser should focus on.
     */
    public BrowserView(final String id, final IModel<Media> mediaModel) {

        super( id, mediaModel );
        setOutputMarkupId( true );

        mediaView = new ListIteratorView<Media>() {

            transient SizedListIterator<Media> iterator;

            private SizedListIterator<Media> getIterator() {

                if (iterator == null)
                    iterator = albumService.iterateMedia( SnaplogSession.get().newToken(), mediaModel.getObject().getAlbum() );

                return iterator;
            }

            @Override
            public int size() {

                return getIterator().size();
            }

            @Override
            protected ListIterator<Media> load() {

                return getIterator();
            }
        };

        add( new MediaView( "media", new LoadableDetachableModel<Media>() {
            @Override
            protected Media load() {

                if (!resetMediaToCurrent())
                    return null;

                return mediaView.current();
            }
        }, Media.Quality.FULLSCREEN, false ) );
        add( new RefreshingView<Media>( "before" ) {

            @Override
            protected Iterator<IModel<Media>> getItemModels() {

                if (!resetMediaToCurrent())
                    return Iterators.emptyIterator();

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

                        BrowserView.this.getModel().setObject( item.getModelObject() );
                        target.addComponent( BrowserView.this );
                    }
                } );
            }
        } );
        add( new RefreshingView<Media>( "after" ) {

            @Override
            protected Iterator<IModel<Media>> getItemModels() {

                if (!resetMediaToCurrent())
                    return Iterators.emptyIterator();

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

                        BrowserView.this.getModel().setObject( item.getModelObject() );
                        target.addComponent( BrowserView.this );
                    }
                } );
            }
        } );
    }

    /**
     * @return <code>true</code> if a current element has been selected in #mediaView or <code>false</code> if there is no media to select.
     */
    boolean resetMediaToCurrent() {

        Media media = getModelObject();
        if (media == null)
            return false;

        // Pick an element in the media view if one hasn't been picked yet.
        if (!mediaView.hasCurrent())
            if (!mediaView.hasNext())
                return false;
            else
                mediaView.next();

        // Find current media in mediaView
        while (mediaView.current().compareTo( media ) > 0)
            mediaView.previous();
        while (mediaView.current().compareTo( media ) < 0)
            mediaView.next();

        return true;
    }
}
