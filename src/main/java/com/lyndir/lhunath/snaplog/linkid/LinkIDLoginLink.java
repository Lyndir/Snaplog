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
import java.util.Locale;

import net.link.safeonline.sdk.auth.util.AuthenticationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;


/**
 * <h2>{@link LinkIDLoginLink}<br>
 * <sub>A link that uses the linkID SDK to log a user in through the linkID authentication services.</sub></h2>
 *
 * <p>
 * <i>Sep 22, 2008</i>
 * </p>
 *
 * @author lhunath
 */
public class LinkIDLoginLink extends AbstractLinkIDAuthLink {

    private static final long serialVersionUID = 1L;
    private String themeName;
    private Boolean forceAuthentication = false;
    private KeyPair keyPair;
    private X509Certificate certificate;


    public LinkIDLoginLink(String id) {

        super( id );
    }

    public LinkIDLoginLink(String id, Class<? extends Page> target) {

        super( id, target );
    }

    public LinkIDLoginLink(String id, Class<? extends Page> target, String themeName, Boolean forceAuthentication,
                           String session) {

        super( id, target );

        this.themeName = themeName;
        this.forceAuthentication = forceAuthentication;
        this.session = session;
    }

    /**
     * @param themeName The themeName of this {@link LinkIDLoginLink}.
     */
    public void setThemeName(String themeName) {

        this.themeName = themeName;
    }

    /**
     * @param forceAuthentication
     */
    public void setForceAuthentication(Boolean forceAuthentication) {

        this.forceAuthentication = forceAuthentication;
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

        String targetUrl = RequestCycle.get().urlFor( target, null ).toString();
        Locale locale = Session.exists()? Session.get().getLocale(): request.getLocale();

        AuthenticationUtils.login( targetUrl, locale, themeName, forceAuthentication, session, keyPair, certificate,
                                   request, response );
    }
}
