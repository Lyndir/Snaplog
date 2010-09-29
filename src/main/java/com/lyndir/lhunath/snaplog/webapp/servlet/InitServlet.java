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

import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.DateUtils;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import java.io.IOException;
import java.util.ListIterator;
import javax.servlet.ServletException;
import javax.servlet.http.*;


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

        if (req.getParameter( "fix" ) != null)
            doTask( resp, "Fixing state", new Runnable() {
                @Override
                public void run() {

                    ListIterator<Album> albumIt = albumServiceProvider.get()
                            .iterateAlbums( SecurityToken.INTERNAL_USE_ONLY, Predicates.<Album>alwaysTrue() );
                    while (albumIt.hasNext()) {

                        Media lastMedia = null;
                        ListIterator<Media> mediaIt = albumServiceProvider.get()
                                .iterateMedia( SecurityToken.INTERNAL_USE_ONLY, albumIt.next(), true );
                        while (mediaIt.hasNext()) {

                            Media media = mediaIt.next();
                            if (lastMedia != null && ObjectUtils.equal( media.getName(), lastMedia.getName() ))
                                try {
                                    logger.inf( "Found duplicate: last=%s, current=%s.  Deleting current.", lastMedia, media );
                                    albumServiceProvider.get().delete( SecurityToken.INTERNAL_USE_ONLY, media );
                                }
                                catch (PermissionDeniedException e) {
                                    logger.bug( e );
                                }

                            else
                                lastMedia = media;
                        }
                    }
                }
            } );

        if (req.getParameter( "media" ) != null)
            doTask( resp, "Loading all album media", new Runnable() {
                @Override
                public void run() {

                    albumServiceProvider.get().loadAllAlbumMedia();
                }
            } );

        if (req.getParameter( "mediaData" ) != null)
            doTask( resp, "Loading all album media data", new Runnable() {
                @Override
                public void run() {

                    albumServiceProvider.get().loadAllAlbumMediaData();
                }
            } );
    }

    private void doTask(final HttpServletResponse resp, final String name, final Runnable task)
            throws IOException {

        DateUtils.Timer timer = DateUtils.startTiming( name );
        resp.getWriter().format( name + "..." );
        resp.flushBuffer();

        try {
            task.run();

            resp.getWriter().format( " done (%s).\n", timer.logFinish( logger ) );
            resp.flushBuffer();
        }

        catch (Throwable t) {
            resp.getWriter().format( " error (%s - %s).\n", t.toString(), timer.logFinish( logger ) );
            resp.flushBuffer();
        }
    }
}
