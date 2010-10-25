package com.lyndir.lhunath.snaplog.data.service.impl.db4o;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Source;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.*;
import com.lyndir.lhunath.snaplog.data.service.*;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import java.util.List;


/**
 * <h2>{@link InitDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 17, 2010</i> </p>
 *
 * @author lhunath
 */
public class InitDAOImpl implements InitDAO {

    static final Logger logger = Logger.get( InitDAOImpl.class );

    private final ObjectContainer db;
    private final UserDAO userDAO;
    private final SourceDAO sourceDAO;

    @Inject
    public InitDAOImpl(final ObjectContainer db, final UserDAO userDAO, final SourceDAO sourceDAO) {

        this.db = db;
        this.userDAO = userDAO;
        this.sourceDAO = sourceDAO;
    }

    @Override
    public void initialize() {

        // Watch for unoptimized queries.
        //        EventRegistry registry = EventRegistryFactory.forObjectContainer( db );
        //        registry.activated().addListener( new EventListener4<ObjectInfoEventArgs>() {
        //            @Override
        //            public void onEvent(final Event4<ObjectInfoEventArgs> e, final ObjectInfoEventArgs args) {
        //
        //                logger.dbg( "[DB4O-ACT-EVENT] %s", args.object() );
        //            }
        //        } );
        //        ((ObjectContainerBase) db).getNativeQueryHandler().addListener( new Db4oQueryExecutionListener() {
        //            @Override
        //            public void notifyQueryExecuted(final NQOptimizationInfo info) {
        //                //if (info.optimized() != null)
        //                logger.dbg( "[DB4O-NQ] %s", info );
        //            }
        //        } );

        // Find default user.
        SnaplogConstants.DEFAULT_USER = new User( new LinkID( "b21e33e2-b63e-4f06-8f52-84509883e1d1" ), "lhunath" );
        ObjectSet<User> defaultUserQuery = db.queryByExample( SnaplogConstants.DEFAULT_USER );
        if (defaultUserQuery.hasNext())
            SnaplogConstants.DEFAULT_USER = defaultUserQuery.next();
        // Configure default user.
        if (!((ExtObjectContainer) db).isActive( SnaplogConstants.DEFAULT_USER ))
            logger.dbg( "Was not active: %s", SnaplogConstants.DEFAULT_USER );
        db.store( SnaplogConstants.DEFAULT_USER );

        // Find default user's profile.
        UserProfile defaultUserProfile;
        ObjectSet<UserProfile> defaultUserProfileQuery = db.query( new Predicate<UserProfile>() {

            @Override
            public boolean match(final UserProfile candidate) {

                return ObjectUtils.equal( candidate.getUser(), SnaplogConstants.DEFAULT_USER );
            }
        } );
        if (defaultUserProfileQuery.hasNext())
            defaultUserProfile = defaultUserProfileQuery.next();
        else
            defaultUserProfile = new UserProfile( SnaplogConstants.DEFAULT_USER );
        // Configure default user's profile.
        defaultUserProfile.getACL().setDefaultPermission( Permission.VIEW );
        if (!((ExtObjectContainer) db).isActive( defaultUserProfile ))
            logger.dbg( "Was not active: %s", defaultUserProfile );
        db.store( defaultUserProfile );

        // Find default user's album.
        List<Source> sources = sourceDAO.listSources( new com.google.common.base.Predicate<Source>() {

            @Override
            public boolean apply(final Source input) {

                return ObjectUtils.equal( input.getOwner(), SnaplogConstants.DEFAULT_USER );
            }
        } );
        SnaplogConstants.DEFAULT_SOURCE =
                sources.isEmpty()? new S3Source( defaultUserProfile, "snaplog.net", "users/lhunath/Life" ): sources.get( 0 );
        // Configure default user's album.
        SnaplogConstants.DEFAULT_SOURCE.getACL().setDefaultPermission( Permission.INHERIT );
        if (!((ExtObjectContainer) db).isActive( SnaplogConstants.DEFAULT_SOURCE ))
            logger.dbg( "Was not active: %s", SnaplogConstants.DEFAULT_SOURCE );
        db.store( SnaplogConstants.DEFAULT_SOURCE );

        logger.dbg( "Default user: %s, profile: %s (ACL: %s), album: %s (ACL: %s)", SnaplogConstants.DEFAULT_USER, defaultUserProfile,
                    defaultUserProfile.getACL(), SnaplogConstants.DEFAULT_SOURCE, SnaplogConstants.DEFAULT_SOURCE.getACL() );
        logger.dbg( "Known users:" );
        for (final User user : db.query( User.class ))
            logger.dbg( "    - %s", user );
        logger.dbg( "Known profiles:" );
        for (final UserProfile userProfile : db.query( UserProfile.class ))
            logger.dbg( "    - %s", userProfile );
        logger.dbg( "Known albums:" );
        for (final Source source : db.query( Source.class ))
            logger.dbg( "    - %s", source );
    }

    @Override
    public void shutdown() {

        // Shut down the database.
        if (!db.ext().isClosed()) {
            db.commit();

            do {
                logger.inf( "Closing the database." );
            }
            while (!db.close());
        }
    }
}
