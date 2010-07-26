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

import com.google.inject.Injector;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.logging.exception.AlreadyCheckedException;
import com.lyndir.lhunath.lib.wayward.js.AjaxHooks;
import com.lyndir.lhunath.lib.wayward.state.ComponentStateListener;
import com.lyndir.lhunath.snaplog.data.object.Issue;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.linkid.SnaplogWebappConfig;
import com.lyndir.lhunath.snaplog.model.service.IssueService;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.filter.OpenCloseTagExpander;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tab.AccessDeniedErrorPage;
import com.lyndir.lhunath.snaplog.webapp.tab.InternalErrorPage;
import com.lyndir.lhunath.snaplog.webapp.tab.NewUserTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.PageExpiredErrorPage;
import net.link.safeonline.sdk.common.configuration.WebappConfig;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.guice.InjectionFlagCachingGuiceComponentInjector;
import org.apache.wicket.markup.IMarkupParserFactory;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.settings.IExceptionSettings;


/**
 * <h2>{@link SnaplogWebApplication}<br> <sub>Wicket {@link WebApplication} for the media album application.</sub></h2>
 *
 * <p> <i>May 31, 2009</i> </p>
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
     * {@inheritDoc}
     */
    @Override
    protected void init() {

        // LinkID setup.
        WebappConfig.setConfig( new SnaplogWebappConfig() );

        // Guice injector.
        Injector injector = GuiceContext.get( getServletContext() );
        addComponentInstantiationListener( new InjectionFlagCachingGuiceComponentInjector( this, injector ) );
        addPreComponentOnBeforeRenderListener( injector.getInstance( AuthenticationListener.class ) );
        addPreComponentOnBeforeRenderListener( new ComponentStateListener( new NewUserTabPanel.NewUserTabActivator() ) );

        // Application setup.
        getApplicationSettings().setPageExpiredErrorPage( PageExpiredErrorPage.class );
        getApplicationSettings().setAccessDeniedPage( AccessDeniedErrorPage.class );
        getApplicationSettings().setInternalErrorPage( InternalErrorPage.class );
        getExceptionSettings().setUnexpectedExceptionDisplay( IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE );

        // https://issues.apache.org/jira/browse/WICKET-2650 -- Consistently create body for short tags.
        getMarkupSettings().setMarkupParserFactory( new IMarkupParserFactory() {

            @Override
            public MarkupParser newMarkupParser(final MarkupResourceStream resource) {

                MarkupParser markupParser = new MarkupParser( resource );
                markupParser.appendMarkupFilter( new OpenCloseTagExpander() );

                return markupParser;
            }
        } );
        getMarkupSettings().setDefaultMarkupEncoding( "UTF-8" );

        // Page mounting.
        mount( new HybridUrlCodingStrategy( "main", LayoutPage.class ) );
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
    public Session newSession(final Request request, final Response response) {

        return new SnaplogSession( request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestCycle newRequestCycle(final Request request, final Response response) {

        return new WebRequestCycle( this, (WebRequest) request, (WebResponse) response ) {

            /**
             * {@inheritDoc}
             */
            @Override
            public Page onRuntimeException(final Page page, final RuntimeException e) {

                Issue issue = null;
                try {
                    try {
                        try {
                            UserProfile profile = null;
                            try {
                                User user = SnaplogSession.get().getActiveUser();
                                if (user != null)
                                    profile = GuiceContext.getInstance( UserService.class )
                                            .getProfile( SnaplogSession.get().newToken(), user );
                            }
                            catch (PermissionDeniedException ee) {
                                throw new AlreadyCheckedException( ee );
                            }

                            issue = new Issue( page, e, profile );
                            GuiceContext.getInstance( IssueService.class ).report( issue );
                        }
                        catch (Exception ee) {
                            // Fallback 1: Don't try to look up subject.
                            logger.bug( ee );

                            issue = new Issue( page, e, null );
                        }
                    }
                    catch (Exception ee) {
                        // Fallback 2: Don't try resolve the exception.
                        logger.bug( ee );

                        issue = new Issue( page, null, null );
                    }
                }
                catch (Exception ee) {
                    // Fallback 3: Just try to get the user to the error page; don't bother with anything extra.
                    logger.bug( ee );
                }

                return new InternalErrorPage( issue );
            }
        };
    }

    @Override
    public AjaxRequestTarget newAjaxRequestTarget(final Page page) {

        AjaxRequestTarget target = super.newAjaxRequestTarget( page );
        AjaxHooks.installAjaxEvents( target );

        if (page instanceof LayoutPage)
            ((LayoutPage) page).addComponents( target );

        return target;
    }
}
