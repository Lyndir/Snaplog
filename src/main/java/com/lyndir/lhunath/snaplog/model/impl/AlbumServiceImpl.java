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

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.AlbumData;
import com.lyndir.lhunath.snaplog.data.media.AlbumProviderType;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.media.MediaTimeFrame.Type;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AlbumProvider;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.SecurityService;


/**
 * <h2>{@link AlbumServiceImpl}</h2>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class AlbumServiceImpl implements AlbumService {

    private static final Logger logger = Logger.get( AlbumServiceImpl.class );

    ObjectContainer db;
    SecurityService securityService;


    /**
     * @param db
     *            See {@link ServicesModule}.
     * @param securityService
     *            See {@link ServicesModule}.
     */
    @Inject
    public AlbumServiceImpl(ObjectContainer db, SecurityService securityService) {

        this.db = db;
        this.securityService = securityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Album findAlbumWithName(final SecurityToken token, final User ownerUser, final String albumName) {

        checkNotNull( ownerUser, "Given ownerUser must not be null." );
        checkNotNull( albumName, "Given album name must not be null." );

        return db.query( new com.db4o.query.Predicate<Album>() {

            @Override
            public boolean match(Album candidate) {

                return candidate.getOwnerUser().equals( ownerUser ) && candidate.getName().equals( albumName )
                       && securityService.hasAccess( Permission.VIEW, token, candidate );
            }
        } ).next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Media findMediaWithName(final SecurityToken token, final Album album, final String mediaName) {

        checkNotNull( album, "Given album must not be null." );
        checkNotNull( mediaName, "Given media name must not be null." );

        ObjectSet<Media> mediaQuery = db.query( new com.db4o.query.Predicate<Media>() {

            @Override
            public boolean match(Media candidate) {

                return candidate.getAlbum().equals( album ) && candidate.getName().endsWith( mediaName )
                       && securityService.hasAccess( Permission.VIEW, token, candidate );
            }
        } );
        if (mediaQuery.hasNext())
            return mediaQuery.next();

        // Media in album by mediaName not found.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<MediaTimeFrame> iterateYears(final SecurityToken token, Album album) {

        // TODO: This method should return an Iterable and should not cache the results.

        checkNotNull( album, "Given album must not be null." );

        AlbumData albumData = getAlbumData( album );
        if (!albumData.hasInternalTimeFrames()) {
            MediaTimeFrame currentYear = null, currentMonth = null, currentDay = null;
            LinkedList<MediaTimeFrame> internalTimeFrames = new LinkedList<MediaTimeFrame>();

            for (Iterator<Media> it = iterateFiles( token, album ); it.hasNext();) {
                Media mediaFile = it.next();
                long shotTime = mediaFile.shotTime();

                if (currentYear == null || !currentYear.containsTime( shotTime ))
                    internalTimeFrames.add( currentYear = new MediaTimeFrame( null, Type.YEAR, shotTime ) );

                if (currentMonth == null || !currentMonth.containsTime( shotTime ))
                    currentYear.addTimeFrame( currentMonth = new MediaTimeFrame( currentYear, Type.MONTH, shotTime ) );

                if (currentDay == null || !currentDay.containsTime( shotTime ))
                    currentMonth.addTimeFrame( currentDay = new MediaTimeFrame( currentMonth, Type.DAY, shotTime ) );

                currentDay.addFile( mediaFile );
            }

            albumData.setInternalTimeFrames( internalTimeFrames );
            db.store( albumData );
        }

        return securityService.iterateTimeFramesFor( token, albumData );
    }

    /**
     * Obtain an {@link AlbumData} entry for the given album.
     * 
     * If there is no data for the {@link Album} yet; an empty data object will be created.
     * 
     * @param album
     *            The album whose data to get.
     * 
     * @return The data for the given album.
     */
    private AlbumData getAlbumData(final Album album) {

        checkNotNull( album, "Given album must not be null." );

        ObjectSet<AlbumData> albumDataQuery = db.query( new com.db4o.query.Predicate<AlbumData>() {

            @Override
            public boolean match(AlbumData candidate) {

                return candidate.getAlbum().equals( album );
            }
        } );
        if (albumDataQuery.hasNext())
            return albumDataQuery.next();

        // No AlbumData yet for this album.
        AlbumData albumData = getAlbumProvider( album ).newAlbumData( album );
        db.store( albumData );

        return albumData;
    }

    private static <A extends Album> AlbumProvider<A, Media> getAlbumProvider(A album) {

        checkNotNull( album, "Given album must not be null." );

        for (AlbumProviderType albumProviderType : AlbumProviderType.values())
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
    public Iterator<Media> iterateFiles(final SecurityToken token, Album album) {

        // TODO: This should return an Iterable and we should check the security conditions as each object is requested.
        // Should probably do away with the cache since we shouldn't check security conditions in advance and cache the
        // result of that.

        checkNotNull( album, "Given album must not be null." );

        // Load the album's media.
        AlbumData albumData = getAlbumData( album );
        if (!albumData.hasInternalFiles()) {
            Iterator<Media> it = getAlbumProvider( album ).iterateFiles( SecurityToken.INTERNAL_USE_ONLY, album );
            albumData.setInternalFiles( Lists.newArrayList( it ) );
            db.store( albumData );
        }

        // Create an iterator that will check permissions for each item.
        return securityService.iterateFilesFor( token, albumData );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getResourceURI(final SecurityToken token, Media media, Quality quality)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );
        checkNotNull( quality, "Given quality must not be null." );

        return getAlbumProvider( media.getAlbum() ).getResourceURI( token, media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(final SecurityToken token, Media media)
            throws PermissionDeniedException {

        checkNotNull( media, "Given media must not be null." );

        return getAlbumProvider( media.getAlbum() ).modifiedTime( token, media );
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated
     */
    @Override
    @Deprecated
    public AlbumData newAlbumData(Album album) {

        throw new UnsupportedOperationException();
    }
}
