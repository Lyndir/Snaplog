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
package com.lyndir.lhunath.snaplog.linkid;

import net.link.safeonline.sdk.common.configuration.PropertiesWebappConfig;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <h2>{@link SnaplogWebappConfig}<br>
 * <sub>Configuration of the snaplog web application for linkID.</sub></h2>
 * 
 * <p>
 * <i>Jan 1, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class SnaplogWebappConfig extends PropertiesWebappConfig {

    static final Logger         logger                     = Logger.get( SnaplogWebappConfig.class );

    private static final String WEBAPP_PROPERTIES_RESOURCE = "webapp.properties";


    /**
     * Create a new {@link SnaplogWebappConfig} instance.
     */
    public SnaplogWebappConfig() {

        super( WEBAPP_PROPERTIES_RESOURCE );

        logger.dbg( "SnaplogWebappConfig resource: %s", Thread.currentThread()
                                                              .getContextClassLoader()
                                                              .getResource( WEBAPP_PROPERTIES_RESOURCE ) );
    }
}
