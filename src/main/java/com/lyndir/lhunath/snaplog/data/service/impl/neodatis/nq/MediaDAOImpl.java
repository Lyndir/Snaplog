package com.lyndir.lhunath.snaplog.data.service.impl.neodatis.nq;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.DateUtils;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3MediaData;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import java.util.*;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;


/**
 * <h2>{@link MediaDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaDAOImpl implements MediaDAO {

    static final Logger logger = Logger.get( MediaDAOImpl.class );

    private final ODB db;

    @Inject
    public MediaDAOImpl(final ODB db) {

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
            Objects<M> results = db.getObjects( new SimpleNativeQuery() {

                public boolean match(final S3Media candidate) {

                    return ObjectUtils.equal( candidate.getAlbum(), album ) && ObjectUtils.equal( candidate.getName(), mediaName );
                }
            } );
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
            Objects<D> results = db.getObjects( new SimpleNativeQuery() {

                public boolean match(final S3MediaData candidate) {

                    return ObjectUtils.equal( candidate.getMedia(), (Media) media );
                }
            } );
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

        Objects<M> results = db.getObjects( new SimpleNativeQuery() {

            public boolean match(final S3Media candidate) {

                return ObjectUtils.equal( candidate.getAlbum(), album );
            }
        } );

        List<M> resultsList = Lists.newLinkedList( results );
        Collections.sort( resultsList, new Comparator<M>() {

            @Override
            public int compare(final M o1, final M o2) {

                return o1.compareTo( o2 ) * (ascending? 1: -1);
            }
        } );

        return resultsList;
    }

    @Override
    public <D extends MediaData<?>> List<D> listMediaData(final Album album, final boolean ascending) {

        Objects<D> results = db.getObjects( new SimpleNativeQuery() {

            public boolean match(final S3MediaData candidate) {

                return ObjectUtils.equal( candidate.getMedia().getAlbum(), album );
            }
        } );
        List<D> resultsList = Lists.newLinkedList( results );
        Collections.sort( resultsList, new Comparator<D>() {

            @Override
            public int compare(final D o1, final D o2) {

                return o1.getMedia().compareTo( o2.getMedia() ) * (ascending? 1: -1);
            }
        } );

        return resultsList;
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

        Objects<MediaMapping> results = db.getObjects( new SimpleNativeQuery() {

            public boolean match(final MediaMapping candidate) {

                return ObjectUtils.equal( candidate.getMapping(), mapping);
            }
        } );
        if (results.hasNext()) {
            MediaMapping result = results.next();
            checkState( !results.hasNext(), "Multiple media mappings found for %s", mapping );

            return result;
        }

        return null;
    }
}
