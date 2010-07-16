package com.lyndir.lhunath.snaplog.data.service.impl.db4o.nq;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.QueryComparator;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.MediaData;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import java.util.List;


/**
 * <h2>{@link MediaDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaDAOImpl implements MediaDAO {

    private final ObjectContainer db;

    @Inject
    public MediaDAOImpl(final ObjectContainer db) {

        this.db = db;
    }

    @Override
    public void update(final Media media) {

        db.store( media );
    }

    @Override
    public void update(final MediaData<?> mediaData) {

        db.store( mediaData );
    }

    @Override
    public <D extends MediaData<?>> D findMediaData(final Album album, final String mediaName) {

        ObjectSet<D> result = db.query( new Predicate<D>() {

            @Override
            public boolean match(final D candidate) {

                Media media = candidate.getMedia();
                return ObjectUtils.equal( media.getAlbum(), album ) && ObjectUtils.equal( media.getName(), mediaName );
            }
        } );
        if (result.hasNext()) {
            checkState( result.size() == 1, "Multiple media data found for %s named %s", album, mediaName );
            return result.next();
        }

        return null;
    }

    @Override
    public <M extends Media> List<M> listMedia(final Album album, final String mediaName, final boolean ascending) {

        checkNotNull( album, "Given album must not be null." );
        checkNotNull( mediaName, "Given media name must not be null." );

        return db.query( new Predicate<M>() {

            @Override
            public boolean match(final M candidate) {

                return ObjectUtils.equal( candidate.getAlbum(), album ) && candidate.getName().endsWith( mediaName );
            }
        }, new QueryComparator<M>() {
            @Override
            public int compare(final M first, final M second) {

                return first.compareTo( second ) * (ascending? 1: -1);
            }
        } );
    }

    @Override
    public <M extends Media> List<M> listMedia(final Album album, final boolean ascending) {

        return db.query( new Predicate<M>() {

            @Override
            public boolean match(final M candidate) {

                return candidate.getAlbum().equals( album );
            }
        }, new QueryComparator<M>() {
            @Override
            public int compare(final M first, final M second) {

                return first.compareTo( second ) * (ascending? 1: -1);
            }
        } );
    }

    @Override
    public <D extends MediaData<?>> List<D> listMediaData(final Album album, final boolean ascending) {

        return db.query( new Predicate<D>() {
            @Override
            public boolean match(final D candidate) {

                return candidate.getMedia().getAlbum().equals( album );
            }
        }, new QueryComparator<D>() {
            @Override
            public int compare(final D first, final D second) {

                return first.getMedia().compareTo( second.getMedia() ) * (ascending? 1: -1);
            }
        } );
    }

    @Override
    public void delete(final Iterable<Media> medias) {

        for (final Media media : medias)
            db.delete( media );
    }
}
