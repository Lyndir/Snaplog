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
package com.lyndir.lhunath.snaplog.model.service.impl;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.QueryEvaluationMode;
import com.db4o.internal.ObjectContainerBase;
import com.db4o.query.Predicate;
import com.db4o.ta.TransparentPersistenceSupport;
import com.google.inject.AbstractModule;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3MediaData;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.AlbumDAO;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.data.service.SecurityDAO;
import com.lyndir.lhunath.snaplog.data.service.UserDAO;
import com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda.AlbumDAOImpl;
import com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda.MediaDAOImpl;
import com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda.SecurityDAOImpl;
import com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda.UserDAOImpl;
import com.lyndir.lhunath.snaplog.model.service.*;
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

        bind( MediaDAO.class ).to( MediaDAOImpl.class );
        bind( AlbumDAO.class ).to( AlbumDAOImpl.class );
        bind( UserDAO.class ).to( UserDAOImpl.class );
        bind( SecurityDAO.class ).to( SecurityDAOImpl.class );

        // Database
        logger.dbg( "Binding database" );
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // TODO: Figure out why transparent persistence doesn't work with S3MediaData's map.
        configuration.common().add( new TransparentPersistenceSupport() );
        configuration.common().updateDepth( 5 );
        configuration.common().queries().evaluationMode( QueryEvaluationMode.LAZY );
        // TODO: Do this smarter; annotations or such.
        configuration.common().objectClass( User.class ).objectField( "userName" ).indexed( true );
        configuration.common().objectClass( User.class ).objectField( "linkID" ).indexed( true );
        configuration.common().objectClass( UserProfile.class ).objectField( "user" ).indexed( true );
        configuration.common().objectClass( Album.class ).objectField( "name" ).indexed( true );
        configuration.common().objectClass( Album.class ).objectField( "ownerProfile" ).indexed( true );
        configuration.common().objectClass( S3Media.class ).objectField( "album" ).indexed( true );
        configuration.common().objectClass( S3MediaData.class ).objectField( "media" ).indexed( true );
        // TODO: NQ optimization isn't working.  Fix it or convert to SODA style or find a way to do better SODA through annotations.
        //        configuration.common().diagnostic().addListener( new DiagnosticListener() {
        //
        //            @Override
        //            public void onDiagnostic(final Diagnostic diagnostic) {
        //                logger.dbg( "[DB4O-DIAG] %s", diagnostic );
        //            }
        //        } );
        EmbeddedObjectContainer db = Db4oEmbedded.openFile( configuration, "snaplog.db4o" );
        bind( ObjectContainer.class ).toInstance( db );

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
