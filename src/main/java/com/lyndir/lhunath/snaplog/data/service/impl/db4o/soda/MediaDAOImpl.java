package com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda;

import static com.google.common.base.Preconditions.checkNotNull;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.util.DateUtils;
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

        DateUtils.startTiming( "updateMedia" );
        try {
            db.store( media );
        }
        finally {
            DateUtils.popTimer().logFinish();
        }
    }

    @Override
    public void update(final MediaData<?> mediaData) {

        DateUtils.startTiming( "updateMediaData" );
        try {
            db.store( mediaData );
        }
        finally {
            DateUtils.popTimer().logFinish();
        }
    }

    @Override
    public <D extends MediaData<?>> D findMediaData(final Album album, final String mediaName) {

        DateUtils.startTiming( "findMediaData" );
        try {
            Query query = db.query();
            query.constrain( MediaData.class ) //
                    .and( query.descend( "media" ).descend( "name" ).constrain( mediaName ) ) //
                    .and( query.descend( "media" ).descend( "album" ).constrain( album ) );

            ObjectSet<D> mediaDataQuery = query.execute();
            if (mediaDataQuery.hasNext())
                return mediaDataQuery.next();

            return null;
        }
        finally {
            DateUtils.popTimer().logFinish();
        }
    }

    @Override
    public <M extends Media> List<M> listMedia(final Album album, final String mediaName) {

        checkNotNull( album, "Given album must not be null." );
        checkNotNull( mediaName, "Given media name must not be null." );

        Query query = db.query();
        query.constrain( Media.class ) //
                // FIXME: Might not work for Media implementations that have no album field?
                .and( query.descend( "album" ).constrain( album ) ) //
                .and( query.descend( "name" ).orderAscending().constrain( mediaName ) );

        return query.execute();
    }

    @Override
    public <M extends Media> List<M> listMedia(final Album album) {

        Query query = db.query();
        query.constrain( Media.class ) //
                .and( query.descend( "album" ).constrain( album ) );
        // TODO: Set the sort order in nq. package too.
        query.descend( "name" ).orderAscending();

        return query.execute();
    }
}
