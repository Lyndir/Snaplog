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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.util.URLUtils;


/**
 * <h2>{@link ImageServlet}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Jan 19, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class ImageServlet extends HttpServlet {

    /**
     * Context-relative path of this servlet.
     */
    public static final String PATH = "/img";

    private static final String PARAM_USER = "u";
    private static final String PARAM_ALBUM = "a";
    private static final String PARAM_MEDIA = "m";
    private static final String PARAM_QUALITY = "q";

    private final UserService userService;
    private final AlbumService albumService;


    /**
     * @param userService  See {@link UserService}
     * @param albumService See {@link AlbumService}
     */
    @Inject
    public ImageServlet(UserService userService, AlbumService albumService) {

        this.userService = userService;
        this.albumService = albumService;
    }

    /**
     * Obtain a context-relative path to the {@link ImageServlet} such that it will render the given media at the given quality.
     *
     * @param media   The media that should be shown at the given URL.
     * @param quality The quality to show the media at.
     *
     * @return A context-relative URL.
     */
    public static String getContextRelativePathFor(Media media, Quality quality) {

        checkNotNull( media );
        checkNotNull( quality );

        Album album = media.getAlbum();
        User user = album.getUser();

        StringBuilder path = new StringBuilder( PATH ).append( '?' );
        path.append( PARAM_USER ).append( '=' ).append( URLUtils.encode( user.getUserName() ) ).append( '&' );
        path.append( PARAM_ALBUM ).append( '=' ).append( URLUtils.encode( album.getName() ) ).append( '&' );
        path.append( PARAM_MEDIA ).append( '=' ).append( URLUtils.encode( media.getName() ) ).append( '&' );
        path.append( PARAM_QUALITY ).append( '=' ).append( URLUtils.encode( quality.getName() ) ).append( '&' );

        return path.substring( 0, path.length() - 1 );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userName = req.getParameter( PARAM_USER );
        String albumName = req.getParameter( PARAM_ALBUM );
        String mediaName = req.getParameter( PARAM_MEDIA );
        String qualityName = req.getParameter( PARAM_QUALITY );

        User user = userService.findUserWithName( userName );
        Album album = albumService.findAlbumWithName( user, albumName );
        Media media = albumService.findMediaWithName( album, mediaName );

        resp.sendRedirect( albumService.getResourceURI( media, Quality.findQualityWithName( qualityName ) )
                .toASCIIString() );
    }
}
