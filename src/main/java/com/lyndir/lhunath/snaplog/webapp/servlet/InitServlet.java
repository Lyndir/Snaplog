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
package com.lyndir.lhunath.snaplog.webapp.servlet;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.Duration;
import org.joda.time.Instant;


/**
 * <h2>{@link InitServlet}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Jan 19, 2010</i> </p>
 *
 * @author lhunath
 */
public class InitServlet extends HttpServlet {

    final Logger logger = Logger.get( InitServlet.class );

    /**
     * Context-relative path of this servlet.
     */
    public static final String PATH = "/init";

    private final Provider<AlbumService> albumServiceProvider;

    /**
     * @param albumServiceProvider See {@link AlbumService}
     */
    @Inject
    public InitServlet(final Provider<AlbumService> albumServiceProvider) {

        this.albumServiceProvider = albumServiceProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        if (req.getParameter( "media" ) != null) {
            Instant start = new Instant();
            logger.inf( "Loading all album media..." );
            resp.getWriter().format( "Loading all album media...\n" );
            resp.flushBuffer();

            albumServiceProvider.get().loadAllAlbumMedia();

            Duration duration = new Duration( start, new Instant() );
            logger.inf( "Done loading all album media (%s).", duration );
            resp.getWriter().format( "Done loading all album media (%s).\n", duration );
            resp.flushBuffer();
        }

        if (req.getParameter( "mediaData" ) != null) {
            Instant start = new Instant();
            logger.inf( "Loading all album media data..." );
            resp.getWriter().format( "Loading all album media data...\n" );
            resp.flushBuffer();

            albumServiceProvider.get().loadAllAlbumMediaData();

            Duration duration = new Duration( start, new Instant() );
            logger.inf( "Done loading all album media data (%s).", duration );
            resp.getWriter().format( "Done loading all album media data (%s).\n", duration );
            resp.flushBuffer();
        }
    }
}
