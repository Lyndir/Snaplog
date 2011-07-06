package com.lyndir.lhunath.snaplog.data.service.impl.neodatis;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Source;
import com.lyndir.lhunath.snaplog.data.object.user.*;
import com.lyndir.lhunath.snaplog.data.service.*;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;


/**
 * <h2>{@link InitDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 17, 2010</i> </p>
 *
 * @author lhunath
 */
public class InitDAOImpl implements InitDAO {

    static final Logger logger = Logger.get( InitDAOImpl.class );

    private final ODB db;
    private final UserDAO userDAO;
    private final SourceDAO sourceDAO;

    @Inject
    public InitDAOImpl(final ODB db, final UserDAO userDAO, final SourceDAO sourceDAO) {

        this.db = db;
        this.userDAO = userDAO;
        this.sourceDAO = sourceDAO;
    }

    @Override
    public void initialize() {

        // Find default user.
        LinkID defaultLinkID = new LinkID( "b21e33e2-b63e-4f06-8f52-84509883e1d1" );
        SnaplogConstants.DEFAULT_USER = userDAO.findUser( defaultLinkID );
        if (SnaplogConstants.DEFAULT_USER == null)
            SnaplogConstants.DEFAULT_USER = new User( defaultLinkID, "lhunath" );
        // Configure default user.
        //        if (db.getObjectId( SnaplogConstants.DEFAULT_USER ) == null)
        //            logger.dbg( "Was not active: %s", SnaplogConstants.DEFAULT_USER );
        db.store( SnaplogConstants.DEFAULT_USER );

        // Find default user's profile.
        UserProfile defaultUserProfile = userDAO.findUserProfile( SnaplogConstants.DEFAULT_USER );
        if (defaultUserProfile == null)
            defaultUserProfile = new UserProfile( SnaplogConstants.DEFAULT_USER );
        // Configure default user's profile.
        defaultUserProfile.getACL().setDefaultPermission( Permission.VIEW );
        //        if (db.getObjectId( defaultUserProfile ) == null)
        //            logger.dbg( "Was not active: %s", defaultUserProfile );
        db.store( defaultUserProfile );

        // Find default user's source.
        SnaplogConstants.DEFAULT_SOURCE = Iterables.get( sourceDAO.listSources( new Predicate<Source>() {
            @Override
            public boolean apply(final Source input) {

                return ObjectUtils.isEqual( input.getOwner(), SnaplogConstants.DEFAULT_USER );
            }
        } ), 0, new S3Source( defaultUserProfile, "snaplog.net", "users/lhunath/Life" ));
        // Configure default user's source.
        SnaplogConstants.DEFAULT_SOURCE.getACL().setDefaultPermission( Permission.NONE );
        //        if (db.getObjectId( SnaplogConstants.DEFAULT_SOURCE ) == null)
        //            logger.dbg( "Was not active: %s", SnaplogConstants.DEFAULT_SOURCE );
        db.store( SnaplogConstants.DEFAULT_SOURCE );

        logger.dbg( "Default user: %s, profile: %s (ACL: %s), source: %s (ACL: %s)", SnaplogConstants.DEFAULT_USER, defaultUserProfile,
                    defaultUserProfile.getACL(), SnaplogConstants.DEFAULT_SOURCE, SnaplogConstants.DEFAULT_SOURCE.getACL() );
        logger.dbg( "Known users:" );
        Objects<User> users = db.getObjects( User.class );
        for (final User user : users)
            logger.dbg( "    - %s", user );
        logger.dbg( "Known profiles:" );
        Objects<UserProfile> userProfiles = db.getObjects( UserProfile.class );
        for (final UserProfile userProfile : userProfiles)
            logger.dbg( "    - %s", userProfile );
        logger.dbg( "Known sources:" );
        Objects<Source> sources = db.getObjects( S3Source.class );
        for (final Source source : sources)
            logger.dbg( "    - %s", source );
    }

    @Override
    public void shutdown() {

        // Shut down the database.
        if (!db.isClosed()) {
            logger.inf( "Closing the database." );
            db.close();
        }
    }
}
