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
import com.db4o.query.Predicate;
import com.google.inject.AbstractModule;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.security.ACL;
import com.lyndir.lhunath.snaplog.data.user.LinkID;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.data.user.UserProfile;
import com.lyndir.lhunath.snaplog.model.*;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import com.lyndir.lhunath.snaplog.webapp.AuthenticationListener;


/**
 * <h2>{@link ServicesModule}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * [description / usage].
 * </p>
 *
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
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

        // Update dummy data.
        ACL.DEFAULT = new User( new LinkID( ACL.class.getCanonicalName() ), "[DEFAULT]" );
        ObjectSet<User> defaultQuery = db.query( new Predicate<User>() {

            @Override
            public boolean match(final User candidate) {

                return candidate.getLinkID().equals( ACL.DEFAULT.getLinkID() );
            }
        } );
        if (defaultQuery.hasNext())
            ACL.DEFAULT = defaultQuery.next();
        else
            db.store( ACL.DEFAULT );

        db.store( SnaplogConstants.DEFAULT_ALBUM );

        SnaplogConstants.DEFAULT_USER = new User( new LinkID( "b21e33e2-b63e-4f06-8f52-84509883e1d1" ), "lhunath" );
        ObjectSet<User> defaultUserQuery = db.queryByExample( SnaplogConstants.DEFAULT_USER );
        if (defaultUserQuery.hasNext())
            SnaplogConstants.DEFAULT_USER = defaultUserQuery.next();
        else
            db.store( SnaplogConstants.DEFAULT_USER );

        UserProfile defaultUserProfile = new UserProfile( SnaplogConstants.DEFAULT_USER );
        ObjectSet<UserProfile> defaultUserProfileQuery = db.queryByExample( defaultUserProfile );
        if (defaultUserProfileQuery.hasNext())
            defaultUserProfile = defaultUserProfileQuery.next();
        else
            db.store( defaultUserProfile );

        SnaplogConstants.DEFAULT_ALBUM = new S3Album( defaultUserProfile, "Life" );
        ObjectSet<Object> defaultAlbumQuery = db.queryByExample( SnaplogConstants.DEFAULT_ALBUM );
        if (defaultAlbumQuery.hasNext())
            SnaplogConstants.DEFAULT_ALBUM = (Album) defaultAlbumQuery.next();
        else
            db.store( SnaplogConstants.DEFAULT_ALBUM );
        SnaplogConstants.DEFAULT_ALBUM.setDescription(
                "<p>Arbitrary snapshots from Maarten's life.</p><p><label>Camera:</label><input value='Canon Powershot Pro1' /></p>" );
    }
}
