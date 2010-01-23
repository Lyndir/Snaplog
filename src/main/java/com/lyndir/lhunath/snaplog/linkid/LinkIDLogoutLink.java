/*
 * SafeOnline project.
 *
 * Copyright 2006-2008 Lin.k N.V. All rights reserved.
 * Lin.k N.V. proprietary/confidential. Use is subject to license terms.
 */
package com.lyndir.lhunath.snaplog.linkid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

import net.link.safeonline.sdk.auth.filter.LoginManager;
import net.link.safeonline.sdk.auth.util.AuthenticationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;


/**
 * <h2>{@link LinkIDLoginLink}<br>
 * <sub>A link that uses the linkID SDK to log a user out of this application and all other applications in its SSO pool through the linkID
 * authentication services.</sub></h2>
 *
 * <p>
 * <i>Sep 22, 2008</i>
 * </p>
 *
 * @author lhunath
 */
public class LinkIDLogoutLink extends AbstractLinkIDAuthLink {

    private static final long serialVersionUID = 1L;
    private static Log LOG = LogFactory.getLog( LinkIDLogoutLink.class );

    private KeyPair keyPair;
    private X509Certificate certificate;


    public LinkIDLogoutLink(String id) {

        super( id );
    }

    public LinkIDLogoutLink(String id, Class<? extends Page> target) {

        super( id, target );
    }

    /**
     * @param keyPair
     */
    public void setKeyPair(KeyPair keyPair) {

        this.keyPair = keyPair;
    }

    /**
     * @param certificate
     */
    public void setCertificate(X509Certificate certificate) {

        this.certificate = certificate;
    }

    /**
     * {@inheritDoc}
     */
    public void delegate(Class<? extends Page> target, HttpServletRequest request, HttpServletResponse response) {

        boolean redirected = false;
        if (LoginManager.isAuthenticated( request )) {
            String targetUrl = RequestCycle.get().urlFor( target, null ).toString();
            LOG.debug( "Logout delegated to linkID with target: " + targetUrl );
            redirected = AuthenticationUtils.logout( targetUrl, session, keyPair, certificate, request, response );
        }

        if (!redirected) {
            LOG.debug( "Logout handeled locally; invalidating session." );
            Session.get().invalidateNow();

            throw new RestartResponseException( target );
        }
    }
}
