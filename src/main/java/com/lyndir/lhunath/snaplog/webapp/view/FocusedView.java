package com.lyndir.lhunath.snaplog.webapp.view;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.ListIteratorView;
import com.lyndir.lhunath.lib.system.collection.SizedListIterator;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
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
 * <h2>{@link FocusedView}<br> <sub>Component that allows users to view media and browse to similar media.</sub></h2>
 *
 * <p> <i>Jan 6, 2010</i> </p>
 *
 * @author lhunath
 */
public class FocusedView extends GenericPanel<Media> {

    static final Logger logger = Logger.get( FocusedView.class );

    static final int SIDE_IMAGES = 7;

    @Inject
    AlbumService albumService;

    Media currentFile;
    final ListIteratorView<Media> mediaView;

    /**
     * Create a new {@link FocusedView} instance.
     *
     * @param id         The wicket ID to put this component in the HTML.
     * @param mediaModel The model contains the {@link Media} that we should focus on.
     */
    public FocusedView(final String id, final IModel<Media> mediaModel) {

        super( id, mediaModel );
        setOutputMarkupId( true );

        mediaView = new ListIteratorView<Media>() {

            transient SizedListIterator<Media> iterator;

            private SizedListIterator<Media> getIterator() {

                if (iterator == null)
                    iterator = albumService.iterateMedia( SnaplogSession.get().newToken(), getModelObject().getAlbum() );

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
                for (int i = 0; i < SIDE_IMAGES; ++i)
                    if (mediaView.hasPrevious())
                        mediaView.previous();

                // Add 6 or up to the current or last media to the models list.
                for (int i = 0; i < SIDE_IMAGES; ++i) {
                    if (ObjectUtils.equal( mediaView.current(), current ))
                        break;
                    if (!mediaView.hasNext())
                        break;

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

                        FocusedView.this.getModel().setObject( item.getModelObject() );
                        target.addComponent( FocusedView.this );
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
                for (int i = 0; i < SIDE_IMAGES; ++i) {
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

                        FocusedView.this.getModel().setObject( item.getModelObject() );
                        target.addComponent( FocusedView.this );
                    }
                } );
            }
        } );
    }

    @Override
    public boolean isVisible() {

        return super.isVisible() && getModelObject() != null;
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
        while (mediaView.current().compareTo( media ) > 0 && mediaView.hasPrevious())
            mediaView.previous();
        while (mediaView.current().compareTo( media ) < 0 && mediaView.hasNext())
            mediaView.next();

        return true;
    }
}
