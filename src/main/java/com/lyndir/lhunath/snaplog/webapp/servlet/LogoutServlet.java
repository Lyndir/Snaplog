/*
 * SafeOnline project.
 *
 * Copyright 2006-2007 Lin.k N.V. All rights reserved.
 * Lin.k N.V. proprietary/confidential. Use is subject to license terms.
 */

package com.lyndir.lhunath.snaplog.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.link.safeonline.sdk.auth.filter.LoginManager;
import net.link.safeonline.sdk.common.servlet.AbstractInjectionServlet;
import net.link.safeonline.util.servlet.annotation.Init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <h2>{@link LogoutServlet}<br>
 * <sub>Logout servlet.</sub></h2>
 * 
 * <p>
 * Servlet that performs a logout for this web application.
 * </p>
 * 
 * <p>
 * <i>Sep 23, 2008</i>
 * </p>
 * 
 * @author wvdhaute
 */
public class LogoutServlet extends AbstractInjectionServlet {

    private static final String LOGOUT_EXIT_PATH = "LogoutExitPath";

    private static final long serialVersionUID = 1L;

    private static final Log  LOG              = LogFactory.getLog(LogoutServlet.class);

    @Init(name = LOGOUT_EXIT_PATH)
    private String            logoutExitPath;


    @Override
    public void invokeGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOG.debug("invoke get");

        LoginManager.invalidateSession(request);
        response.sendRedirect(logoutExitPath);
    }
}
