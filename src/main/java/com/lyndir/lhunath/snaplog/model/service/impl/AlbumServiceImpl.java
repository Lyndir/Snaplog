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
package com.lyndir.lhunath.snaplog.model.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.SizedListIterator;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.DateUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.AlbumProviderType;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.service.AlbumDAO;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.AlbumProvider;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;


/**
 * <h2>{@link AlbumServiceImpl}</h2>
 *
 * <p> <i>Jul 25, 2009</i> </p>
 *
 * @author lhunath
 */
public class AlbumServiceImpl implements AlbumService {

    private static final Logger logger = Logger.get( AlbumServiceImpl.class );

    private final MediaDAO mediaDAO;
    final SecurityService securityService;
    private final AlbumDAO albumDAO;

    /**
     * @param albumDAO        See {@link ServicesModule}.
     * @param mediaDAO        See {@link ServicesModule}.
     * @param securityService See {@link ServicesModule}.
     */
    @Inject
    public AlbumServiceImpl(final AlbumDAO albumDAO, final MediaDAO mediaDAO, final SecurityService securityService) {

        this.albumDAO = albumDAO;
        this.mediaDAO = mediaDAO;
        this.securityService = securityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SizedListIterator<Album> iterateAlbums(final SecurityToken token, final Predicate<Album> predicate) {

        List<Album> results = albumDAO.listAlbums( predicate );
        return SizedListIterator.of( securityService.filterAccess( Permission.VIEW, token, results.listIterator() ), results.size() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Album findAlbumWithName(final SecurityToken token, final User ownerUser, final String albumName) {

        return securityService.filterAccess( Permission.VIEW, token, albumDAO.listAlbums( ownerUser, albumName ).iterator() ).next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Media findMediaWithName(final SecurityToken token, final Album album, final String mediaName) {

        Iterator<Media> results = securityService.filterAccess( Permission.VIEW, token, mediaDAO.listMedia( album, mediaName ).iterator() );
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
    public void loadMedia(final Album album) {

        getAlbumProvider( album ).loadMedia( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMediaData(final Album album) {

        getAlbumProvider( album ).loadMediaData( album );
    }

    @Override
    public void loadAllAlbumMedia() {

        for (final Album album : albumDAO.listAlbums())
            loadMedia( album );
    }

    @Override
    public void loadAllAlbumMediaData() {

        for (final Album album : albumDAO.listAlbums())
            loadMediaData( album );
    }

    @Override
    public SizedListIterator<Media> iterateMedia(final SecurityToken token, final Album album) {

        List<Media> results = mediaDAO.listMedia( album );
        return SizedListIterator.of( securityService.filterAccess( Permission.VIEW, token, results.listIterator() ), results.size() );
    }

    @Override
    public Iterator<MediaTimeFrame> iterateMediaTimeFrames(final SecurityToken token, final Album album, final DateTimeFieldType frame) {

        return new AbstractIterator<MediaTimeFrame>() {

            private final Iterator<Media> media = iterateMedia( token, album );
            public Media lastMedia;

            @Override
            protected MediaTimeFrame computeNext() {

                if (!media.hasNext())
                    return endOfData();

                ImmutableList.Builder<Media> frameMedia = ImmutableList.builder();
                if (lastMedia == null)
                    lastMedia = media.next();

                // The offset and range of the frame.
                ReadableInstant offset = DateUtils.truncate( lastMedia.shotTime(), frame );
                ReadablePeriod range = DateUtils.period( offset, offset.toInstant().toDateTime().property( frame ).addToCopy( 1 ), frame );

                do {
                    frameMedia.add( lastMedia );
                    if (!media.hasNext())
                        break;
                    lastMedia = media.next();
                } // Continue to add this lastMedia to the current list of media while its shotTime truncates to the offset.
                while (DateUtils.truncate( lastMedia.shotTime(), frame ).equals( offset ));

                return new MediaTimeFrame( offset, range, frameMedia.build() );
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL findResourceURL(final SecurityToken token, final Media media, final Quality quality)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );

        return getAlbumProvider( media.getAlbum() ).findResourceURL( token, media, quality );
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

        albumDAO.update( album );
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
