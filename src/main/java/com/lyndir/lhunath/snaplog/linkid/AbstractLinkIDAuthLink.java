/*
 * SafeOnline project.
 *
 * Copyright 2006-2008 Lin.k N.V. All rights reserved.
 * Lin.k N.V. proprietary/confidential. Use is subject to license terms.
 */
package com.lyndir.lhunath.snaplog.linkid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Application;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;


/**
 * <h2>{@link AbstractLinkIDAuthLink}<br>
 * <sub>A link that uses the linkID SDK to log a user in through the linkID authentication services.</sub></h2>
 * 
 * <p>
 * <i>Sep 22, 2008</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class AbstractLinkIDAuthLink extends Link<Object> implements LinkIDAuthDelegate {

    private static final long serialVersionUID = 1L;

    Class<? extends Page>     requestTarget;
    boolean                   login;
    String                    session;

    LinkIDAuthDelegate        delegate;


    public AbstractLinkIDAuthLink(String id) {

        this( id, null );
    }

    /**
     * @param id
     *            The wicket ID of this link in the webpage.
     * @param target
     *            The {@link Page} to return to after the linkID delegation. <code>null</code>: Use the application's
     *            homepage.
     */
    public AbstractLinkIDAuthLink(String id, Class<? extends Page> target) {

        super( id );

        if (target != null)
            requestTarget = target;

        delegate = this;
    }

    /**
     * @param session
     *            The session of this {@link LinkIDLoginLink}.
     */
    public void setSession(String session) {

        this.session = session;
    }

    @Override
    public void onClick() {

        throw new RedirectResponseException( new IRequestTarget() {

            public void detach(RequestCycle requestCycle) {

            }

            public void respond(RequestCycle requestCycle) {

                HttpServletRequest request = ((WebRequest) requestCycle.getRequest()).getHttpServletRequest();
                HttpServletResponse response = ((WebResponse) requestCycle.getResponse()).getHttpServletResponse();

                Class<? extends Page> target = requestTarget == null? Application.get().getHomePage(): requestTarget;

                // The SDK does the rest.
                delegate.delegate( target, request, response );
            }
        } );

    }
}
