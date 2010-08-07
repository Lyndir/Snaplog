package com.lyndir.lhunath.snaplog.data.service.impl.db4o;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.InitDAO;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;


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

    @Inject
    public InitDAOImpl(final ObjectContainer db) {

        this.db = db;
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
        ObjectSet<Album> defaultAlbumQuery = db.query( new Predicate<Album>() {

            @Override
            public boolean match(final Album candidate) {

                return ObjectUtils.equal( candidate.getOwner(), SnaplogConstants.DEFAULT_USER ) && ObjectUtils.equal( "Life",
                                                                                                                      candidate.getName() );
            }
        } );
        if (defaultAlbumQuery.hasNext())
            SnaplogConstants.DEFAULT_ALBUM = defaultAlbumQuery.next();
        else
            SnaplogConstants.DEFAULT_ALBUM = new S3Album( defaultUserProfile, "Life" );
        // Configure default user's album.
        SnaplogConstants.DEFAULT_ALBUM.setOwnerProfile( defaultUserProfile );
        SnaplogConstants.DEFAULT_ALBUM.getACL().setDefaultPermission( Permission.NONE );
        SnaplogConstants.DEFAULT_ALBUM
                .setDescription(
                        "<p>Arbitrary snapshots from Maarten's life.</p><p><label>Camera:</label><input value='Canon Powershot Pro1' /></p>" );
        if (!((ExtObjectContainer) db).isActive( SnaplogConstants.DEFAULT_ALBUM ))
            logger.dbg( "Was not active: %s", SnaplogConstants.DEFAULT_ALBUM );
        db.store( SnaplogConstants.DEFAULT_ALBUM );

        logger.dbg( "Default user: %s, profile: %s (ACL: %s), album: %s (ACL: %s)", SnaplogConstants.DEFAULT_USER, defaultUserProfile,
                    defaultUserProfile.getACL(), SnaplogConstants.DEFAULT_ALBUM, SnaplogConstants.DEFAULT_ALBUM.getACL() );
        logger.dbg( "Known users:" );
        for (final User user : db.query( User.class ))
            logger.dbg( "    - %s", user );
        logger.dbg( "Known profiles:" );
        for (final UserProfile userProfile : db.query( UserProfile.class ))
            logger.dbg( "    - %s", userProfile );
        logger.dbg( "Known albums:" );
        for (final Album album : db.query( Album.class ))
            logger.dbg( "    - %s", album );
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
