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
package com.lyndir.lhunath.snaplog.model;

import com.google.inject.AbstractModule;
import com.lyndir.lhunath.opal.security.service.SecurityService;
import com.lyndir.lhunath.opal.security.service.impl.SecurityServiceImpl;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.service.*;
import com.lyndir.lhunath.snaplog.model.service.impl.*;


/**
 * <h2>{@link ServiceModule}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public class ServiceModule extends AbstractModule {

    static final Logger logger = Logger.get( ServiceModule.class );

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {

        // Services
        logger.dbg( "Binding services" );
        bind( SourceDelegate.class );
        bind( AWSSourceService.class ).to( AWSSourceServiceImpl.class );
        bind( AWSService.class ).to( AWSServiceImpl.class );
        bind( SecurityService.class ).to( SecurityServiceImpl.class );
        bind( UserService.class ).to( UserServiceImpl.class );
        bind( IssueService.class ).to( IssueServiceImpl.class );
        bind( TagService.class ).to( TagServiceImpl.class );
    }
}
