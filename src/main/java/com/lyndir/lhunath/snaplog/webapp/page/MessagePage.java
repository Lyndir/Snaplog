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
package com.lyndir.lhunath.snaplog.webapp.page;

import java.text.MessageFormat;

import org.apache.wicket.RedirectToUrlException;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.StringHeaderContributor;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link MessagePage}<br>
 * <sub>Abstract page class for unexpected application errors.</sub></h2>
 *
 * <p>
 * This page just shows an error message and redirects back to the homepage after five seconds.
 * </p>
 *
 * <p>
 * <i>Jun 10, 2009</i>
 * </p>
 *
 * @author lhunath
 */
public class MessagePage extends LayoutPage {

    /**
     * Default page constructor.
     */
    public MessagePage() {

        add( new HeaderContributor( new StringHeaderContributor(
                MessageFormat.format( "<meta http-equiv=\"refresh\" content=\"{0};url={1}\" />", getRedirectDelay(),
                                      getRedirectUrl() ) ) ) );

        ((Panel) get( "contentPanel" )).add( new Link<String>( "redirectLink" ) {

            @Override
            public void onClick() {

                throw new RedirectToUrlException( getRedirectUrl() );
            }
        } );
    }

    /**
     * @return Amount of seconds to show the error page for before redirecting to the {@link #getRedirectUrl()}.
     */
    protected int getRedirectDelay() {

        return 10;
    }

    /**
     * @return The URL to redirect to after having shown the error page.
     */
    protected String getRedirectUrl() {

        return "?";
    }
}
