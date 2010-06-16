package com.lyndir.lhunath.snaplog.data.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
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
    public List<User> listUsers() {

        return db.query( User.class );
    }

    @Override
    public List<User> listUsers(final Predicate<User> predicate) {

        return db.query( new com.db4o.query.Predicate<User>() {

            @Override
            public boolean match(final User candidate) {

                return predicate.apply( candidate );
            }
        } );
    }

    @Override
    public UserProfile findUserProfile(final User user) {

        checkNotNull( user, "Given user must not be null." );

        ObjectSet<UserProfile> userProfiles = db.query( new com.db4o.query.Predicate<UserProfile>() {

            @Override
            public boolean match(final UserProfile candidate) {

                return candidate.getUser().equals( user );
            }
        } );

        if (userProfiles.hasNext())
            return userProfiles.next();

        return null;
    }
}
