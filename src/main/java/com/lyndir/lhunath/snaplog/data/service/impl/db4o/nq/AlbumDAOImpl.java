package com.lyndir.lhunath.snaplog.data.service.impl.db4o.nq;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
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

        ObjectSet<Album> result = db.query( new Predicate<Album>() {

            @Override
            public boolean match(final Album candidate) {

                return ObjectUtils.equal( candidate.getOwnerProfile().getUser(), ownerUser ) && ObjectUtils.equal( candidate.getName(),
                                                                                                                   albumName );
            }
        } );
        if (result.hasNext()) {
            checkState( result.size() == 1, "Multiple albums found for %s named %s", ownerUser, albumName );
            return result.next();
        }

        return null;
    }

    @Override
    public List<Album> listAlbums(final com.google.common.base.Predicate<Album> predicate) {

        checkNotNull( predicate, "Given predicate must not be null." );

        return db.query( new Predicate<Album>() {

            @Override
            public boolean match(final Album candidate) {

                return predicate.apply( candidate );
            }
        } );
    }

    @Override
    public List<Album> listAlbums() {

        return db.query( Album.class );
    }
}
