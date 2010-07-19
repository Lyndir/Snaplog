package com.lyndir.lhunath.snaplog.data.service.impl.neodatis;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.AlbumDAO;
import com.lyndir.lhunath.snaplog.data.service.InitDAO;
import com.lyndir.lhunath.snaplog.data.service.UserDAO;
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
    private final AlbumDAO albumDAO;

    @Inject
    public InitDAOImpl(final ODB db, final UserDAO userDAO, final AlbumDAO albumDAO) {

        this.db = db;
        this.userDAO = userDAO;
        this.albumDAO = albumDAO;
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

        // Find default user's album.
        SnaplogConstants.DEFAULT_ALBUM = albumDAO.findAlbum( SnaplogConstants.DEFAULT_USER, "Life" );
        if (SnaplogConstants.DEFAULT_ALBUM == null)
            SnaplogConstants.DEFAULT_ALBUM = new S3Album( defaultUserProfile, "Life" );
        // Configure default user's album.
        SnaplogConstants.DEFAULT_ALBUM.setOwnerProfile( defaultUserProfile );
        SnaplogConstants.DEFAULT_ALBUM.getACL().setDefaultPermission( Permission.VIEW );
        SnaplogConstants.DEFAULT_ALBUM
                .setDescription(
                        "<p>Arbitrary snapshots from Maarten's life.</p><p><label>Camera:</label><input value='Canon Powershot Pro1' /></p>" );
//        if (db.getObjectId( SnaplogConstants.DEFAULT_ALBUM ) == null)
//            logger.dbg( "Was not active: %s", SnaplogConstants.DEFAULT_ALBUM );
        db.store( SnaplogConstants.DEFAULT_ALBUM );

        logger.dbg( "Default user: %s, profile: %s (ACL: %s), album: %s (ACL: %s)", SnaplogConstants.DEFAULT_USER, defaultUserProfile,
                    defaultUserProfile.getACL(), SnaplogConstants.DEFAULT_ALBUM, SnaplogConstants.DEFAULT_ALBUM.getACL() );
        logger.dbg( "Known users:" );
        Objects<User> users = db.getObjects( User.class );
        for (final User user : users)
            logger.dbg( "    - %s", user );
        logger.dbg( "Known profiles:" );
        Objects<UserProfile> userProfiles = db.getObjects( UserProfile.class );
        for (final UserProfile userProfile : userProfiles)
            logger.dbg( "    - %s", userProfile );
        logger.dbg( "Known albums:" );
        Objects<Album> albums = db.getObjects( S3Album.class );
        for (final Album album : albums)
            logger.dbg( "    - %s", album );
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
