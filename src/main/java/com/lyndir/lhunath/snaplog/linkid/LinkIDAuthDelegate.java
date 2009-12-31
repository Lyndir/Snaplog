/*
 * SafeOnline project.
 * 
 * Copyright 2006-2009 Lin.k N.V. All rights reserved.
 * Lin.k N.V. proprietary/confidential. Use is subject to license terms.
 */
package com.lyndir.lhunath.snaplog.linkid;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Page;


/**
 * <h2>{@link LinkIDAuthDelegate}<br>
 * <sub>Simple interface to provide delegation of linkID authentication requests.</sub></h2>
 * 
 * <p>
 * <i>Apr 3, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public interface LinkIDAuthDelegate extends Serializable {

    /**
     * Override this method to implement or delegate the actual linkID operation.
     * 
     * @param target
     *            The wicket page to return to after linkID delegation.
     * @param response
     *            The HTTP response on which to write the delegation result.
     * @param request
     *            The HTTP request which requested this delegation.
     */
    void delegate(Class<? extends Page> target, HttpServletRequest request, HttpServletResponse response);
}
