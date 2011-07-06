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
import com.lyndir.lhunath.snaplog.security.SnaplogST;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.DateUtils;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.model.service.SourceService;
import com.lyndir.lhunath.snaplog.model.service.impl.SourceDelegate;
import java.io.IOException;
import java.util.Iterator;
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

    private final Provider<SourceDelegate> sourceDelegateProvider;

    /**
     * @param sourceDelegateProvider See {@link SourceService}
     */
    @Inject
    public InitServlet(final Provider<SourceDelegate> sourceDelegateProvider) {

        this.sourceDelegateProvider = sourceDelegateProvider;
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

                    SourceDelegate sourceDelegate = sourceDelegateProvider.get();
                    Iterator<Source> sourceIt = sourceDelegate.iterateSources(
                            SnaplogST.INTERNAL_USE_ONLY,
                                                                              Predicates.<Source>alwaysTrue() );
                    while (sourceIt.hasNext()) {

                        Media lastMedia = null;
                        ListIterator<Media> mediaIt = sourceDelegate.iterateMedia( SnaplogST.INTERNAL_USE_ONLY, sourceIt.next(), true );
                        while (mediaIt.hasNext()) {

                            Media media = mediaIt.next();
                            if (lastMedia != null && ObjectUtils.isEqual( media.getName(), lastMedia.getName() ))
                                try {
                                    logger.inf( "Found duplicate: last=%s, current=%s.  Deleting current.", lastMedia, media );
                                    sourceDelegate.delete( SnaplogST.INTERNAL_USE_ONLY, media );
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
            doTask( resp, "Loading all source media", new Runnable() {
                @Override
                public void run() {

                    SourceDelegate sourceDelegate = sourceDelegateProvider.get();
                    Iterator<Source> sourceIt = sourceDelegate.iterateSources(
                            SnaplogST.INTERNAL_USE_ONLY,
                                                                              Predicates.<Source>alwaysTrue() );
                    while (sourceIt.hasNext()) {
                        Source source = sourceIt.next();
                        try {
                            sourceDelegate.loadMedia( SnaplogST.INTERNAL_USE_ONLY, source );
                        }
                        catch (PermissionDeniedException e) {
                            logger.err( e, "While loading media for source %s", source );
                        }
                    }
                }
            } );

        if (req.getParameter( "mediaData" ) != null)
            doTask( resp, "Loading all source media data", new Runnable() {
                @Override
                public void run() {

                    SourceDelegate sourceDelegate = sourceDelegateProvider.get();
                    Iterator<Source> sourceIt = sourceDelegate.iterateSources(
                            SnaplogST.INTERNAL_USE_ONLY,
                                                                              Predicates.<Source>alwaysTrue() );
                    while (sourceIt.hasNext()) {
                        Source source = sourceIt.next();
                        try {
                            sourceDelegate.loadMediaData( SnaplogST.INTERNAL_USE_ONLY, sourceIt.next() );
                        }
                        catch (PermissionDeniedException e) {
                            logger.err( e, "While loading media data for source %s", source );
                        }
                    }
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
            logger.err( t, "While running task %s", name );
            resp.getWriter().format( " error (%s - %s).\n", t.toString(), timer.logFinish( logger ) );
            resp.flushBuffer();
        }
    }
}
