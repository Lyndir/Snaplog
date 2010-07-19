package com.lyndir.lhunath.snaplog.data.service.impl.neodatis.criteria;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.service.AlbumDAO;
import java.util.List;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.impl.core.query.criteria.EqualCriterion;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;


/**
 * <h2>{@link AlbumDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class AlbumDAOImpl implements AlbumDAO {

    // FIXME: Only querying for S3 implementations.  See <https://sourceforge.net/tracker/?func=detail&aid=3031161&group_id=179124&atid=887885>
    private final ODB db;

    @Inject
    public AlbumDAOImpl(final ODB db) {

        this.db = db;
    }

    @Override
    public void update(final Album album) {

        db.store( album );
    }

    @Override
    public Album findAlbum(final User ownerUser, final String albumName) {

        checkNotNull( ownerUser, "Given ownerUser must not be null." );
        checkNotNull( albumName, "Given album name must not be null." );

        Objects<Album> results = db.getObjects( new ValuesCriteriaQuery( S3Album.class, //
                                                                         new EqualCriterion( "ownerProfile.user", ownerUser ) //
                                                                                 .and( new EqualCriterion( "name", albumName ) ) ) );
        if (results.hasNext()) {
            Album result = results.next();
            checkState( !results.hasNext(), "Multiple albums found for %s named %s", ownerUser, albumName );

            return result;
        }

        return null;
    }

    @Override
    public List<Album> listAlbums(final Predicate<Album> predicate) {

        checkNotNull( predicate, "Given predicate must not be null." );

        return ImmutableList.copyOf( Collections2.filter( listAlbums(), predicate ) );
    }

    @Override
    public List<Album> listAlbums() {

        Objects<Album> results = db.getObjects( S3Album.class );

        return ImmutableList.copyOf( results );
    }
}
