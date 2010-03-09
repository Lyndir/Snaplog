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
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.model.AWSMediaProviderService;
import com.lyndir.lhunath.snaplog.model.AWSService;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.UserService;
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
        bind( new TypeLiteral<AlbumService<Provider>>() {} ).to( new TypeLiteral<AlbumServiceImpl<Provider>>() {} );
        bind( new TypeLiteral<AWSMediaProviderService>() {} ).to( AWSMediaProviderServiceImpl.class );
        bind( new TypeLiteral<AWSService>() {} ).to( AWSServiceImpl.class );
        bind( new TypeLiteral<UserService<Provider>>() {} ).to( new TypeLiteral<UserServiceImpl<Provider>>() {} );

        // Database
        logger.dbg( "Binding database" );
        EmbeddedObjectContainer db = Db4oEmbedded.openFile( "snaplog.db4o" );
        bind( ObjectContainer.class ).toInstance( db );
    }
}
