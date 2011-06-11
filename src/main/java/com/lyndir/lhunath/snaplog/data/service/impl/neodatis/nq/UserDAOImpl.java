package com.lyndir.lhunath.snaplog.data.service.impl.neodatis.nq;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.UserDAO;
import java.util.List;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;


/**
 * <h2>{@link UserDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class UserDAOImpl implements UserDAO {

    private final ODB db;

    @Inject
    public UserDAOImpl(final ODB db) {

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

        Objects<User> results = db.getObjects( new SimpleNativeQuery() {

            public boolean match(final User candidate) {

                return ObjectUtils.isEqual( candidate.getLinkID(), linkID );
            }
        } );

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

        Objects<UserProfile> results = db.getObjects( new SimpleNativeQuery() {

            public boolean match(final UserProfile candidate) {

                return ObjectUtils.isEqual( candidate.getUser(), user );
            }
        } );

        if (results.hasNext()) {
            UserProfile result = results.next();
            checkState( !results.hasNext(), "Multiple profiles found for %s", user );

            return result;
        }

        return null;
    }

    @Override
    public List<User> listUsers() {

        Objects<User> results = db.getObjects( User.class );
        return ImmutableList.copyOf( results );
    }

    @Override
    public List<User> listUsers(final Predicate<User> predicate) {

        Objects<User> results = db.getObjects( new SimpleNativeQuery() {

            public boolean match(final User candidate) {

                return predicate.apply( candidate );
            }
        } );
        return ImmutableList.copyOf( results );
    }
}
