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
package com.lyndir.lhunath.snaplog.webapp.listener;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.lyndir.lhunath.snaplog.model.impl.ServicesModule;
import com.lyndir.lhunath.snaplog.webapp.SnaplogWebApplication;
import com.lyndir.lhunath.snaplog.webapp.servlet.AppLogoutServlet;
import com.lyndir.lhunath.snaplog.webapp.servlet.ImageServlet;
import net.link.safeonline.sdk.auth.servlet.LoginServlet;
import net.link.safeonline.sdk.auth.servlet.LogoutServlet;
import org.apache.wicket.protocol.http.WicketFilter;


/**
 * <h2>{@link GuiceListener}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * [description / usage].
 * </p>
 *
 * <p>
 * <i>Jan 11, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class GuiceListener extends GuiceServletContextListener {

    private static final String PATH_WICKET = "/*";
    private static final String PATH_LINKID_LOGIN = "/login";
    public static final String PATH_LINKID_LOGOUT = "/logout";


    /**
     * {@inheritDoc}
     */
    @Override
    protected Injector getInjector() {

        return Guice.createInjector( Stage.DEVELOPMENT, new ServicesModule(), new ServletModule() {

            @Override
            protected void configureServlets() {

                Builder<String, String> paramBuilder;

                // Wicket
                paramBuilder = new ImmutableMap.Builder<String, String>();
                paramBuilder.put( "applicationClassName", SnaplogWebApplication.class.getCanonicalName() );
                paramBuilder.put( "filterMappingUrlPattern", PATH_WICKET );
                filter( PATH_WICKET ).through( WicketFilter.class, paramBuilder.build() );
                bind( WicketFilter.class ).in( Scopes.SINGLETON );

                // Snaplog Image Servlet
                serve( ImageServlet.PATH ).with( ImageServlet.class );
                bind( ImageServlet.class ).in( Scopes.SINGLETON );

                // Snaplog Logout Servlet
                paramBuilder = new ImmutableMap.Builder<String, String>();
                paramBuilder.put( AppLogoutServlet.PARAM_LOGOUT_EXIT_PATH, PATH_LINKID_LOGOUT );
                serve( AppLogoutServlet.PATH ).with( AppLogoutServlet.class, paramBuilder.build() );
                bind( AppLogoutServlet.class ).in( Scopes.SINGLETON );

                // LinkID Login Landing Servlet
                paramBuilder = new ImmutableMap.Builder<String, String>();
                paramBuilder.put( "ErrorPage", SnaplogWebApplication.PATH_LINKID_ERROR );
                serve( PATH_LINKID_LOGIN ).with( LoginServlet.class, paramBuilder.build() );
                bind( LoginServlet.class ).in( Scopes.SINGLETON );

                // LinkID Logout Landing Servlet
                paramBuilder = new ImmutableMap.Builder<String, String>();
                paramBuilder.put( "LogoutPath", AppLogoutServlet.PATH );
                paramBuilder.put( "ErrorPage", SnaplogWebApplication.PATH_LINKID_ERROR );
                serve( PATH_LINKID_LOGOUT ).with( LogoutServlet.class, paramBuilder.build() );
                bind( LogoutServlet.class ).in( Scopes.SINGLETON );
            }
        } );
    }
}
