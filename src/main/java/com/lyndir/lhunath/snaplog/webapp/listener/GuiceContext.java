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
import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.servlet.DisableURLSessionFilter;
import com.lyndir.lhunath.snaplog.data.DAOModule;
import com.lyndir.lhunath.snaplog.model.ServiceModule;
import com.lyndir.lhunath.snaplog.webapp.SnaplogWebApplication;
import com.lyndir.lhunath.snaplog.webapp.servlet.AppLogoutServlet;
import com.lyndir.lhunath.snaplog.webapp.servlet.InitServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import net.link.safeonline.sdk.auth.servlet.LoginServlet;
import net.link.safeonline.sdk.auth.servlet.LogoutServlet;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.servlet.WicketSessionFilter;


/**
 * <h2>{@link GuiceContext}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Jan 11, 2010</i> </p>
 *
 * @author lhunath
 */
public class GuiceContext extends GuiceServletContextListener {

    static final Logger logger = Logger.get( GuiceContext.class );

    private static Injector injector;
    private static final String PATH_ALL = "/*";
    private static final String PATH_WICKET = PATH_ALL;
    private static final String PATH_LINKID_LOGIN = "/login";
    private static final String PATH_LINKID_LOGOUT = "/logout";

    static final Key<WicketFilter> wicketFilter = Key.get( WicketFilter.class );

    /**
     * {@inheritDoc}
     */
    @Override
    protected Injector getInjector() {

        return Guice.createInjector( Stage.DEVELOPMENT, new DAOModule(), new ServiceModule(), new ServletModule() {

            @Override
            protected void configureServlets() {

                Builder<String, String> paramBuilder;

                // Disable jsessionid in URLs.
                filter( PATH_ALL ).through( DisableURLSessionFilter.class );
                bind( DisableURLSessionFilter.class ).in( Scopes.SINGLETON );

                // Wicket
                paramBuilder = new ImmutableMap.Builder<String, String>();
                paramBuilder.put( ContextParamWebApplicationFactory.APP_CLASS_PARAM, SnaplogWebApplication.class.getCanonicalName() );
                paramBuilder.put( WicketFilter.FILTER_MAPPING_PARAM, PATH_WICKET );
                filter( PATH_WICKET ).through( wicketFilter, paramBuilder.build() );
                bind( WicketFilter.class ).in( Scopes.SINGLETON );

                paramBuilder = new ImmutableMap.Builder<String, String>();
                paramBuilder.put( "filterName", wicketFilter.toString() );
                // filter( ImageServlet.PATH ).through( WicketSessionFilter.class, paramBuilder.build() );
                bind( WicketSessionFilter.class ).in( Scopes.SINGLETON );

                // Snaplog Image Servlet
                // serve( ImageServlet.PATH ).with( ImageServlet.class );
                // bind( ImageServlet.class ).in( Scopes.SINGLETON );

                // Snaplog Init Servlet
                serve( InitServlet.PATH ).with( InitServlet.class );
                bind( InitServlet.class ).in( Scopes.SINGLETON );

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

    /**
     * @param servletContextEvent The servlet context event that a listener was invoked for.  If <code>null</code>, behaves like #get().
     *
     * @return The Guice {@link Injector} that was added to the given event's {@link ServletContext} on initialization.
     */
    public static Injector get(final ServletContextEvent servletContextEvent) {

        if (servletContextEvent == null)
            return get();

        return (Injector) servletContextEvent.getServletContext().getAttribute( Injector.class.getName() );
    }

    /**
     * @param servletContext The request's servlet context.  If <code>null</code>, behaves like #get().
     *
     * @return The Guice {@link Injector} that was added to the given {@link ServletContext} on initialization.
     */
    public static Injector get(final ServletContext servletContext) {

        if (servletContext == null)
            return get();

        return (Injector) servletContext.getAttribute( Injector.class.getName() );
    }

    /**
     * @return The Guice {@link Injector} that was created for the {@link WebApplication} this thread is working with.
     *
     * @see Application#get()
     */
    public static Injector get() {

        if (injector != null)
            return injector;

        return get( ((WebApplication) Application.get( wicketFilter.toString() )).getServletContext() );
    }

    /**
     * This method should only be used from outside servlet contexts.
     *
     * @param injector The Guice injector to return from #get
     */
    public static void setInjector(final Injector injector) {

        GuiceContext.injector = injector;
    }

    /**
     * Convenience method for <code>get().getInstance( type );</code>.
     *
     * @param type The type to inject.
     * @param <T>  The type of the type to inject.
     *
     * @return The injected instance of the given type.
     */
    public static <T> T getInstance(final Class<T> type) {

        return get().getInstance( type );
    }
}
