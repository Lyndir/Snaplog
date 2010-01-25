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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Guice;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame.Type;
import com.lyndir.lhunath.snaplog.data.aws.S3Album;
import com.lyndir.lhunath.snaplog.model.AWSMediaProviderService;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.MediaProviderService;


/**
 * <h2>{@link AlbumService}<br>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class AlbumServiceImpl implements AlbumService {

    private static final Logger                 logger     = Logger.get( AlbumServiceImpl.class );

    private static final Map<Album, AlbumCache> albumCache = new HashMap<Album, AlbumCache>();


    /**
     * {@inheritDoc}
     */
    @Override
    public Album findAlbumWithName(User user, String albumName) {

        checkNotNull( user );
        checkNotNull( albumName );

        for (Album album : albumCache.keySet())
            if (album.getUser().equals( user ) && album.getName().equals( albumName ))
                return album;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Media findMediaWithName(Album album, String mediaName) {

        checkNotNull( album );
        checkNotNull( mediaName );

        for (Media file : getFiles( album ))
            if (file.getName().equals( mediaName ))
                return file;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MediaTimeFrame> getYears(Album album) {

        checkNotNull( album );

        ImmutableList<MediaTimeFrame> timeFrames = getAlbumCache( album ).getTimeFrames();
        if (timeFrames != null)
            return timeFrames;

        MediaTimeFrame currentYear = null, currentMonth = null, currentDay = null;
        Builder<MediaTimeFrame> timeFramesBuilder = new ImmutableList.Builder<MediaTimeFrame>();

        for (Media mediaFile : getFiles( album )) {
            long shotTime = mediaFile.shotTime();

            if (currentYear == null || !currentYear.containsTime( shotTime ))
                timeFramesBuilder.add( currentYear = new MediaTimeFrame( null, Type.YEAR, shotTime ) );

            if (currentMonth == null || !currentMonth.containsTime( shotTime ))
                currentYear.addTimeFrame( currentMonth = new MediaTimeFrame( currentYear, Type.MONTH, shotTime ) );

            if (currentDay == null || !currentDay.containsTime( shotTime ))
                currentMonth.addTimeFrame( currentDay = new MediaTimeFrame( currentMonth, Type.DAY, shotTime ) );

            currentDay.addFile( mediaFile );
        }

        getAlbumCache( album ).setTimeFrames( timeFrames = timeFramesBuilder.build() );
        return timeFrames;
    }

    /**
     * Obtain an {@link AlbumCache} entry for the given album.
     * 
     * If the {@link Album} is not yet cached; it will be added to the cache. This method is guaranteed to not return
     * <code>null</code>s.
     * 
     * @param album
     *            The album whose cache to get.
     * @return The cache for the given album.
     */
    private AlbumCache getAlbumCache(Album album) {

        checkNotNull( album );

        AlbumCache cache = albumCache.get( album );
        if (cache == null)
            albumCache.put( album, cache = new AlbumCache() );

        return cache;
    }

    private static AlbumProvider getAlbumProvider(Album album) {

        checkNotNull( album );

        for (AlbumProvider albumProvider : AlbumProvider.values())
            if (albumProvider.getAlbumType().isAssignableFrom( album.getClass() ))
                return albumProvider;

        throw logger.err( "Could not find a provider for the album type: %s", album.getClass() ) //
                    .toError( IllegalArgumentException.class );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<? extends Media> getFiles(Album album) {

        checkNotNull( album );

        ImmutableList<? extends Media> files = getAlbumCache( album ).getFiles();
        if (files != null)
            return files;

        getAlbumCache( album ).setFiles( files = getAlbumProvider( album ).getFiles( album ) );
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getResourceURI(Media media, Quality quality) {

        checkNotNull( media );
        checkNotNull( quality );

        return getAlbumProvider( media.getAlbum() ).getResourceURI( media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(Media media) {

        checkNotNull( media );

        return getAlbumProvider( media.getAlbum() ).modifiedTime( media );
    }


    public enum AlbumProvider implements MediaProviderService<Album, Media> {

        AMAZON_S3(S3Album.class, AWSMediaProviderService.class);

        private final Class<? extends Album>                              albumType;
        private final Class<? extends MediaProviderService<Album, Media>> albumProviderServiceType;


        @SuppressWarnings("unchecked")
        AlbumProvider(Class<? extends Album> albumType,
                      Class<? extends MediaProviderService<? extends Album, ? extends Media>> albumProviderServiceType) {

            this.albumType = checkNotNull( albumType );
            this.albumProviderServiceType = checkNotNull( (Class<? extends MediaProviderService<Album, Media>>) albumProviderServiceType );
        }

        /**
         * @return The albumType of this {@link AlbumProvider}.
         */
        public Class<? extends Album> getAlbumType() {

            return albumType;
        }

        /**
         * @return The albumProviderService of this {@link AlbumProvider}.
         */
        public MediaProviderService<Album, Media> getAlbumProviderService() {

            return Guice.createInjector( new ServicesModule() ).getInstance( albumProviderServiceType );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ImmutableList<? extends Media> getFiles(Album album) {

            checkNotNull( album );

            return getAlbumProviderService().getFiles( album );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public URI getResourceURI(Media media, Quality quality) {

            checkNotNull( media );
            checkNotNull( quality );

            return getAlbumProviderService().getResourceURI( media, quality );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long modifiedTime(Media media) {

            checkNotNull( media );

            return getAlbumProviderService().modifiedTime( media );
        }
    }


    protected class AlbumCache {

        private ImmutableList<? extends Media> files;
        private ImmutableList<MediaTimeFrame>  timeFrames;


        /**
         * @param files
         *            The files of this {@link AlbumCache}.
         */
        public void setFiles(ImmutableList<? extends Media> files) {

            checkNotNull( files );

            this.files = files;
        }

        /**
         * @return The files of this {@link AlbumCache}.
         */
        public ImmutableList<? extends Media> getFiles() {

            return files;
        }

        /**
         * @param timeFrames
         *            The timeFrames of this {@link AlbumCache}.
         */
        public void setTimeFrames(ImmutableList<MediaTimeFrame> timeFrames) {

            checkNotNull( timeFrames );

            this.timeFrames = timeFrames;
        }

        /**
         * @return The timeFrames of this {@link AlbumCache}.
         */
        public ImmutableList<MediaTimeFrame> getTimeFrames() {

            return timeFrames;
        }
    }
}
