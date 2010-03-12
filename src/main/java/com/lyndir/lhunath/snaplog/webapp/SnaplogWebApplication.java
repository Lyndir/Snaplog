/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.snaplog.webapp;

import net.link.safeonline.sdk.common.configuration.WebappConfig;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.guice.InjectionFlagCachingGuiceComponentInjector;
import org.apache.wicket.markup.IMarkupParserFactory;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.settings.IExceptionSettings;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.linkid.SnaplogWebappConfig;
import com.lyndir.lhunath.snaplog.webapp.error.AccessDeniedErrorPage;
import com.lyndir.lhunath.snaplog.webapp.error.InternalErrorPage;
import com.lyndir.lhunath.snaplog.webapp.error.Issue;
import com.lyndir.lhunath.snaplog.webapp.error.PageExpiredErrorPage;
import com.lyndir.lhunath.snaplog.webapp.filter.OpenCloseTagExpander;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceInjector;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;


/**
 * <h2>{@link SnaplogWebApplication}<br>
 * <sub>Wicket {@link WebApplication} for the media album application.</sub></h2>
 * 
 * <p>
 * <i>May 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class SnaplogWebApplication extends WebApplication {

    static final Logger logger = Logger.get( SnaplogWebApplication.class );

    /**
     * Context-relative path to the page that indicates an error occurred during the linkID authentication protocol.
     * 
     * TODO: Add a page for this.
     */
    public static final String PATH_LINKID_ERROR = "/linkid-error";

    /**
     * Metadata key for an {@link Issue} describing a {@link RuntimeException} that occurred.
     * 
     * @see RequestCycle#getMetaData(MetaDataKey)
     */
    public static final MetaDataKey<Issue> METADATA_RUNTIME_EXCEPTION_ISSUE = new MetaDataKey<Issue>() {};


    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {

        WebappConfig.setConfig( new SnaplogWebappConfig() );

        getApplicationSettings().setPageExpiredErrorPage( PageExpiredErrorPage.class );
        getApplicationSettings().setAccessDeniedPage( AccessDeniedErrorPage.class );
        getApplicationSettings().setInternalErrorPage( InternalErrorPage.class );

        getExceptionSettings().setUnexpectedExceptionDisplay( IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE );

        // https://issues.apache.org/jira/browse/WICKET-2650 -- Consistently create body for short tags.
        getMarkupSettings().setMarkupParserFactory( new IMarkupParserFactory() {

            @Override
            public MarkupParser newMarkupParser(MarkupResourceStream resource) {

                MarkupParser markupParser = new MarkupParser( resource );
                markupParser.appendMarkupFilter( new OpenCloseTagExpander() );

                return markupParser;
            }
        } );
        getMarkupSettings().setDefaultMarkupEncoding( "UTF-8" );

        addComponentInstantiationListener( new InjectionFlagCachingGuiceComponentInjector( this, GuiceInjector.get() ) );
        addPreComponentOnBeforeRenderListener( GuiceInjector.get().getInstance( AuthenticationListener.class ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Page> getHomePage() {

        return LayoutPage.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session newSession(Request request, Response response) {

        return new SnaplogSession( request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestCycle newRequestCycle(Request request, Response response) {

        return new WebRequestCycle( this, (WebRequest) request, (WebResponse) response ) {

            /**
             * {@inheritDoc}
             */
            @Override
            public Page onRuntimeException(Page page, RuntimeException e) {

                setMetaData( METADATA_RUNTIME_EXCEPTION_ISSUE, new Issue( page, e ) );

                return super.onRuntimeException( page, e );
            }
        };
    }
}
