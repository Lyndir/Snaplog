package com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.UserDAO;
import java.util.List;


/**
 * <h2>{@link UserDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class UserDAOImpl implements UserDAO {

    private final ObjectContainer db;

    @Inject
    public UserDAOImpl(final ObjectContainer db) {

        this.db = db;
    }

    @Override
    public void update(final User user) {

        db.store( user );
    }

    @Override
    public void update(final UserProfile userProfile) {

        db.store( userProfile );
    }

    @Override
    public User findUser(final LinkID linkID) {

        checkNotNull( linkID, "Given linkID must not be null." );

        Query query = db.query();
        query.constrain( User.class ) //
                .and( query.descend( "linkID" ).constrain( linkID ) );

        ObjectSet<User> results = query.execute();
        if (results.hasNext()) {
            User result = results.next();
            checkState( !results.hasNext(), "Multiple users found for %s", linkID );

            return result;
        }

        return null;
    }

    @Override
    public UserProfile findUserProfile(final User user) {

        checkNotNull( user, "Given user must not be null." );

        Query query = db.query();
        query.constrain( UserProfile.class ) //
                .and( query.descend( "user" ).constrain( user ) );

        ObjectSet<UserProfile> results = query.execute();
        if (results.hasNext()) {
            UserProfile result = results.next();
            checkState( !results.hasNext(), "Multiple profiles found for %s", user );

            return result;
        }

        return null;
    }

    @Override
    public List<User> listUsers() {

        Query query = db.query();
        query.constrain( User.class );

        return query.execute();
    }

    @Override
    public List<User> listUsers(final com.google.common.base.Predicate<User> predicate) {

        // TODO: Can't SODA this.  Maybe we should avoid this method altogether?
        return db.query( new Predicate<User>() {

            @Override
            public boolean match(final User candidate) {

                return predicate.apply( candidate );
            }
        } );
    }
}
