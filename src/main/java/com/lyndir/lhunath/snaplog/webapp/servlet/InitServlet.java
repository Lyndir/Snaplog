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
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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

    private final AlbumService albumService;

    /**
     * @param albumService See {@link AlbumService}
     */
    @Inject
    public InitServlet(final AlbumService albumService) {

        this.albumService = albumService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        albumService.syncAllAlbums();
    }
}
