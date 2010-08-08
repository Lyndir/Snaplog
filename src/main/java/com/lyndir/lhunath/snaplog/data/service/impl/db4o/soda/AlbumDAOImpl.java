package com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.service.AlbumDAO;
import java.util.List;


/**
 * <h2>{@link AlbumDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class AlbumDAOImpl implements AlbumDAO {

    private final ObjectContainer db;

    @Inject
    public AlbumDAOImpl(final ObjectContainer db) {

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

        Query query = db.query();
        query.constrain( Album.class ) //
                .and( query.descend( "name" ).constrain( albumName ) ) //
                .and( query.descend( "ownerProfile" ).descend( "owner" ).constrain( ownerUser ) );

        ObjectSet<Album> results = query.execute();
        if (results.hasNext()) {
            Album result = results.next();
            checkState( !results.hasNext(), "Multiple albums found for %s named %s", ownerUser, albumName );

            return result;
        }

        return null;
    }

    @Override
    public List<Album> listAlbums(final com.google.common.base.Predicate<Album> predicate) {

        checkNotNull( predicate, "Given predicate must not be null." );

        // TODO: Can't SODA this.  Maybe we should avoid this method altogether?
        return db.query( new Predicate<Album>() {

            @Override
            public boolean match(final Album candidate) {

                return predicate.apply( candidate );
            }
        } );
    }

    @Override
    public List<Album> listAlbums() {

        Query query = db.query();
        query.constrain( Album.class );

        return query.execute();
    }
}
