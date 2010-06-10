/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.snaplog.model.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.SizedListIterator;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.AlbumProviderType;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.AlbumProvider;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.SecurityService;
import java.net.URL;
import java.util.Iterator;


/**
 * <h2>{@link AlbumServiceImpl}</h2>
 *
 * <p> <i>Jul 25, 2009</i> </p>
 *
 * @author lhunath
 */
public class AlbumServiceImpl implements AlbumService {

    private static final Logger logger = Logger.get( AlbumServiceImpl.class );

    final ObjectContainer db;
    final SecurityService securityService;

    /**
     * @param db              See {@link ServicesModule}.
     * @param securityService See {@link ServicesModule}.
     */
    @Inject
    public AlbumServiceImpl(final ObjectContainer db, final SecurityService securityService) {

        this.db = db;
        this.securityService = securityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SizedListIterator<Album> iterateAlbums(final SecurityToken token, final Predicate<Album> predicate) {

        checkNotNull( predicate, "Given predicate must not be null." );

        ObjectSet<Album> results = db.query( new com.db4o.query.Predicate<Album>() {

            @Override
            public boolean match(final Album candidate) {

                return predicate.apply( candidate );
            }
        } );
        return SizedListIterator.of( securityService.filterAccess( Permission.VIEW, token, results.listIterator() ), results.size() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Album findAlbumWithName(final SecurityToken token, final User ownerUser, final String albumName) {

        checkNotNull( ownerUser, "Given ownerUser must not be null." );
        checkNotNull( albumName, "Given album name must not be null." );

        return securityService.filterAccess( Permission.VIEW, token, db.query( new com.db4o.query.Predicate<Album>() {

            @Override
            public boolean match(final Album candidate) {

                return ObjectUtils.equal( candidate.getOwnerProfile().getUser(), ownerUser ) && ObjectUtils.equal( candidate.getName(),
                                                                                                                   albumName );
            }
        } ).iterator() ).next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Media findMediaWithName(final SecurityToken token, final Album album, final String mediaName) {

        checkNotNull( album, "Given album must not be null." );
        checkNotNull( mediaName, "Given media name must not be null." );

        Iterator<Media> results = securityService.filterAccess( Permission.VIEW, token, db.query( new com.db4o.query.Predicate<Media>() {

            @Override
            public boolean match(final Media candidate) {

                return ObjectUtils.equal( candidate.getAlbum(), album ) && candidate.getName().endsWith( mediaName );
            }
        } ).iterator() );
        if (results.hasNext())
            return results.next();

        // Media in album by mediaName not found.
        return null;
    }

    private static <A extends Album> AlbumProvider<A, Media> getAlbumProvider(final A album) {

        checkNotNull( album, "Given album must not be null." );

        for (final AlbumProviderType albumProviderType : AlbumProviderType.values())
            if (albumProviderType.getAlbumProvider().getAlbumType().isAssignableFrom( album.getClass() )) {

                @SuppressWarnings("unchecked")
                AlbumProvider<A, Media> checkedAlbumProvider = (AlbumProvider<A, Media>) albumProviderType.getAlbumProvider();

                return checkedAlbumProvider;
            }

        throw logger.err( "Could not find a provider for the album type: %s", album.getClass() ) //
                .toError( IllegalArgumentException.class );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncMedia(final Album album) {

        getAlbumProvider( album ).syncMedia( album );
    }

    @Override
    public void syncAllAlbums() {

        for (final Album album : db.query( Album.class ))
            syncMedia( album );
    }

    @Override
    public SizedListIterator<Media> iterateMedia(final SecurityToken token, final Album album) {

        ObjectSet<Media> results = db.query( new com.db4o.query.Predicate<Media>() {

            @Override
            public boolean match(final Media candidate) {

                return candidate.getAlbum().equals( album );
            }
        } );
        return SizedListIterator.of( securityService.filterAccess( Permission.VIEW, token, results.listIterator() ), results.size() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResourceURL(final SecurityToken token, final Media media, final Quality quality)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );

        return getAlbumProvider( media.getAlbum() ).getResourceURL( token, media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(final SecurityToken token, final Media media)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );

        return getAlbumProvider( media.getAlbum() ).modifiedTime( token, media );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAlbum(final SecurityToken token, final Album album)
            throws PermissionDeniedException {

        checkNotNull( album );

        securityService.assertAccess( Permission.CONTRIBUTE, token, album.getOwnerProfile() );

        db.store( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public Album newAlbum(final User ownerUser, final String albumName, final String albumDescription) {

        throw new UnsupportedOperationException();
    }
}
