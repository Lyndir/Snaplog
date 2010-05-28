/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.model.impl;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.internal.ObjectContainerBase;
import com.db4o.internal.query.Db4oQueryExecutionListener;
import com.db4o.internal.query.NQOptimizationInfo;
import com.db4o.query.Predicate;
import com.google.inject.AbstractModule;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.user.LinkID;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.data.user.UserProfile;
import com.lyndir.lhunath.snaplog.model.*;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import com.lyndir.lhunath.snaplog.webapp.AuthenticationListener;


/**
 * <h2>{@link ServicesModule}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public class ServicesModule extends AbstractModule {

    static final Logger logger = Logger.get( ServicesModule.class );

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {

        bind( AuthenticationListener.class );

        // Services
        logger.dbg( "Binding services" );
        bind( AlbumService.class ).to( AlbumServiceImpl.class );
        bind( AWSMediaProviderService.class ).to( AWSMediaProviderServiceImpl.class );
        bind( AWSService.class ).to( AWSServiceImpl.class );
        bind( SecurityService.class ).to( SecurityServiceImpl.class );
        bind( UserService.class ).to( UserServiceImpl.class );

        // Database
        logger.dbg( "Binding database" );
        EmbeddedObjectContainer db = Db4oEmbedded.openFile( "snaplog.db4o" );
        bind( ObjectContainer.class ).toInstance( db );

        // Watch for unoptimized queries.
        ((ObjectContainerBase) db).getNativeQueryHandler().addListener( new Db4oQueryExecutionListener() {
            @Override
            public void notifyQueryExecuted(final NQOptimizationInfo info) {
                //if (NativeQueryHandler.UNOPTIMIZED.equals(info.optimized()))
                //logger.dbg( "%s", info );
            }
        } );

        // Find default user.
        SnaplogConstants.DEFAULT_USER = new User( new LinkID( "b21e33e2-b63e-4f06-8f52-84509883e1d1" ), "lhunath" );
        ObjectSet<User> defaultUserQuery = db.queryByExample( SnaplogConstants.DEFAULT_USER );
        if (defaultUserQuery.hasNext())
            SnaplogConstants.DEFAULT_USER = defaultUserQuery.next();
        // Configure default user.
        if (!((ObjectContainerBase) db).isActive( SnaplogConstants.DEFAULT_USER ))
            logger.dbg( "Was not active: %s", SnaplogConstants.DEFAULT_USER );
        db.store( SnaplogConstants.DEFAULT_USER );

        // Find default user's profile.
        UserProfile defaultUserProfile;
        ObjectSet<UserProfile> defaultUserProfileQuery = db.query( new Predicate<UserProfile>() {
            @Override
            public boolean match(final UserProfile candidate) {

                return candidate.getUser().equals( SnaplogConstants.DEFAULT_USER );
            }
        } );
        if (defaultUserProfileQuery.hasNext())
            defaultUserProfile = defaultUserProfileQuery.next();
        else
            defaultUserProfile = new UserProfile( SnaplogConstants.DEFAULT_USER );
        // Configure default user's profile.
        defaultUserProfile.getACL().setDefaultPermission( Permission.VIEW );
        if (!((ObjectContainerBase) db).isActive( defaultUserProfile ))
            logger.dbg( "Was not active: %s", defaultUserProfile );
        db.store( defaultUserProfile );

        // Find default user's album.
        ObjectSet<Album> defaultAlbumQuery = db.query( new Predicate<Album>() {
            @Override
            public boolean match(final Album candidate) {

                return candidate.getOwner().equals( SnaplogConstants.DEFAULT_USER ) && "Life".equals( candidate.getName() );
            }
        } );
        if (defaultAlbumQuery.hasNext())
            SnaplogConstants.DEFAULT_ALBUM = defaultAlbumQuery.next();
        else
            SnaplogConstants.DEFAULT_ALBUM = new S3Album( defaultUserProfile, "Life" );
        // Configure default user's album.
        SnaplogConstants.DEFAULT_ALBUM.setOwnerProfile( defaultUserProfile );
        SnaplogConstants.DEFAULT_ALBUM.getACL().setDefaultPermission( Permission.VIEW );
        SnaplogConstants.DEFAULT_ALBUM
                .setDescription(
                        "<p>Arbitrary snapshots from Maarten's life.</p><p><label>Camera:</label><input value='Canon Powershot Pro1' /></p>" );
        if (!((ObjectContainerBase) db).isActive( SnaplogConstants.DEFAULT_ALBUM ))
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

        logger.inf( "Guice services initialization completed." );
    }
}
