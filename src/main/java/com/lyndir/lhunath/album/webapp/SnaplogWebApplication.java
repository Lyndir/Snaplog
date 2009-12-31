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
package com.lyndir.lhunath.album.webapp;

import org.apache.wicket.Page;
import org.apache.wicket.markup.IMarkupParserFactory;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.resource.loader.BundleStringResourceLoader;
import org.apache.wicket.settings.IExceptionSettings;

import com.lyndir.lhunath.album.messages.MessagesBundle;
import com.lyndir.lhunath.album.webapp.error.AccessDeniedErrorPage;
import com.lyndir.lhunath.album.webapp.error.InternalErrorPage;
import com.lyndir.lhunath.album.webapp.error.PageExpiredErrorPage;
import com.lyndir.lhunath.album.webapp.filter.OpenCloseTagExpander;
import com.lyndir.lhunath.album.webapp.page.LayoutPage;


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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {

        getResourceSettings().addStringResourceLoader( new BundleStringResourceLoader( MessagesBundle.class.getCanonicalName() ) );

        getApplicationSettings().setPageExpiredErrorPage( PageExpiredErrorPage.class );
        getApplicationSettings().setAccessDeniedPage( AccessDeniedErrorPage.class );
        getApplicationSettings().setInternalErrorPage( InternalErrorPage.class );

        getExceptionSettings().setUnexpectedExceptionDisplay( IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE );

        // https://issues.apache.org/jira/browse/WICKET-2650 -- Consistently create body for short tags.
        getMarkupSettings().setMarkupParserFactory( new IMarkupParserFactory() {

            public MarkupParser newMarkupParser(MarkupResourceStream resource) {

                MarkupParser markupParser = new MarkupParser( resource );
                markupParser.appendMarkupFilter( new OpenCloseTagExpander() );

                return markupParser;
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Page> getHomePage() {

        return LayoutPage.class;
    }
}
