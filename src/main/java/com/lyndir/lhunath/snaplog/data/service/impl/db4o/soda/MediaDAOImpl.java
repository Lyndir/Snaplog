package com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda;

import static com.google.common.base.Preconditions.checkState;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.DateUtils;
import com.lyndir.lhunath.snaplog.data.object.media.*;
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

    static final Logger logger = Logger.get( MediaDAOImpl.class );

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
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public void update(final MediaData<?> mediaData) {

        DateUtils.startTiming( "updateMediaData" );
        try {
            db.store( mediaData );
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public void update(final Iterable<MediaData<?>> mediaDatas) {

        DateUtils.startTiming( "updateMediaDatas" );
        try {
            db.store( mediaDatas );
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public <M extends Media> M findMedia(final Album album, final String mediaName) {

        DateUtils.startTiming( "findMedia" );
        try {
            Query query = db.query();
            query.constrain( Media.class ) //
                    .and( query.descend( "name" ) //
                                  .constrain( mediaName ) ) //
                    .and( query.descend( "album" ) //
                                  .constrain( album ) );

            ObjectSet<M> results = query.execute();
            if (results.hasNext()) {
                M result = results.next();
                checkState( !results.hasNext(), "Multiple media data found for %s named %s", album, mediaName );

                return result;
            }

            return null;
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public <D extends MediaData<M>, M extends Media> D findMediaData(final M media) {

        DateUtils.startTiming( "findMediaData" );
        try {
            Query query = db.query();
            query.constrain( MediaData.class ) //
                    .and( query.descend( "media" ) //
                                  .constrain( media ) );

            ObjectSet<D> results = query.execute();
            if (results.hasNext()) {
                D result = results.next();
                checkState( !results.hasNext(), "Multiple media data found for %s", media );

                return result;
            }

            return null;
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public <M extends Media> List<M> listMedia(final Album album, final boolean ascending) {

        Query query = db.query();
        query.constrain( Media.class ) //
                .and( query.descend( "album" ) //
                              .constrain( album ).identity() );

        // TODO: Set the sort order in nq. package too.
        if (ascending)
            query.descend( "name" ).orderAscending();
        else
            query.descend( "name" ).orderDescending();

        return query.execute();
    }

    @Override
    public <D extends MediaData<?>> List<D> listMediaData(final Album album, final boolean ascending) {

        Query query = db.query();
        query.constrain( MediaData.class ) //
                .and( query.descend( "media" ).descend( "album" ) //
                              .constrain( album ).identity() );

        // TODO: Set the sort order in nq. package too.
        if (ascending)
            query.descend( "media" ).descend( "name" ).orderAscending();
        else
            query.descend( "media" ).descend( "name" ).orderDescending();

        return query.execute();
    }

    @Override
    public <M extends Media> void delete(final Iterable<M> medias) {

        for (final M media : medias)
            db.delete( media );
    }

    @Override
    public MediaMapping newMapping(final MediaMapping mapping) {

        db.store( mapping );

        return mapping;
    }

    @Override
    public MediaMapping findMediaMapping(final String mapping) {

        Query query = db.query();
        query.constrain( MediaMapping.class ) //
                .and( query.descend( "mapping" ) //
                              .constrain( mapping ) );

        ObjectSet<MediaMapping> results = query.execute();
        if (results.hasNext()) {
            MediaMapping result = results.next();
            checkState( !results.hasNext(), "Multiple media mappings found for %s", mapping );

            return result;
        }

        return null;
    }
}
