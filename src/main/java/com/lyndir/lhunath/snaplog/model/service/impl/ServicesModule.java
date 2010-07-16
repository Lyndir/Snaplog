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

import com.db4o.ObjectContainer;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.service.AlbumDAO;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.data.service.SecurityDAO;
import com.lyndir.lhunath.snaplog.data.service.UserDAO;
import com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda.*;
import com.lyndir.lhunath.snaplog.model.provider.Db4oProvider;
import com.lyndir.lhunath.snaplog.model.service.*;
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
        bind( ObjectContainer.class ).toProvider( Db4oProvider.class ).in( Scopes.SINGLETON );
    }
}
