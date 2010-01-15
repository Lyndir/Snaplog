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

    private static final Logger                            logger          = Logger.get( AlbumServiceImpl.class );
    private static final Map<Album, List<? extends Media>> albumFiles      = new HashMap<Album, List<? extends Media>>();
    private static final Map<Album, List<MediaTimeFrame>>  albumTimeFrames = new HashMap<Album, List<MediaTimeFrame>>();


    /**
     * {@inheritDoc}
     */
    public List<MediaTimeFrame> getYears(Album album) {

        checkNotNull( album );

        List<MediaTimeFrame> timeFrames = albumTimeFrames.get( album );
        if (timeFrames != null)
            return timeFrames;

        MediaTimeFrame currentYear = null, currentMonth = null, currentDay = null;
        Builder<MediaTimeFrame> timeFramesBuilder = new ImmutableList.Builder<MediaTimeFrame>();

        for (Media mediaFile : getFiles( album )) {
            long shotTime = mediaFile.shotTime();

            if (currentYear == null || !currentYear.containsTime( shotTime ))
                timeFramesBuilder.add( currentYear = new MediaTimeFrame( null, Type.YEAR, shotTime ) );

            if (currentMonth == null || !currentMonth.containsTime( shotTime ))
                currentYear.add( currentMonth = new MediaTimeFrame( currentYear, Type.MONTH, shotTime ) );

            if (currentDay == null || !currentDay.containsTime( shotTime ))
                currentMonth.add( currentDay = new MediaTimeFrame( currentMonth, Type.DAY, shotTime ) );

            currentDay.addFile( mediaFile );
        }

        albumTimeFrames.put( album, timeFrames = timeFramesBuilder.build() );
        return timeFrames;
    }

    private AlbumProvider getAlbumProvider(Album album) {

        for (AlbumProvider albumProvider : AlbumProvider.values())
            if (albumProvider.getAlbumType().isAssignableFrom( album.getClass() ))
                return albumProvider;

        throw logger.err( "Could not find a provider for the album type: %s", album.getClass() ) //
                    .toError( IllegalArgumentException.class );
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends Media> getFiles(Album album) {

        checkNotNull( album );

        List<? extends Media> files = albumFiles.get( album );
        if (files != null)
            return files;

        files = getAlbumProvider( album ).getFiles( album );
        albumFiles.put( album, files );

        return files;
    }

    /**
     * {@inheritDoc}
     */
    public URI getResourceURI(Media media, Quality quality) {

        return getAlbumProvider( media.getAlbum() ).getResourceURI( media, quality );
    }

    /**
     * {@inheritDoc}
     */
    public long modifiedTime(Media media) {

        return getAlbumProvider( media.getAlbum() ).modifiedTime( media );
    }


    public static enum AlbumProvider implements MediaProviderService<Album, Media> {
        AMAZON_S3(S3Album.class, AWSMediaProviderService.class);

        private Class<? extends Album>                              albumType;
        private Class<? extends MediaProviderService<Album, Media>> albumProviderServiceType;


        @SuppressWarnings("unchecked")
        private AlbumProvider(
                              Class<? extends Album> albumType,
                              Class<? extends MediaProviderService<? extends Album, ? extends Media>> albumProviderServiceType) {

            this.albumType = checkNotNull( albumType );
            this.albumProviderServiceType = checkNotNull( (Class<? extends MediaProviderService<Album, Media>>) albumProviderServiceType );
        }

        /**
         * @return The albumType of this {@link AlbumServiceImpl.AlbumProvider}.
         */
        public Class<? extends Album> getAlbumType() {

            return albumType;
        }

        /**
         * @return The albumProviderService of this {@link AlbumServiceImpl.AlbumProvider}.
         */
        public MediaProviderService<Album, Media> getAlbumProviderService() {

            return Guice.createInjector( new ServicesModule() ).getInstance( albumProviderServiceType );
        }

        /**
         * {@inheritDoc}
         */
        public List<? extends Media> getFiles(Album album) {

            return getAlbumProviderService().getFiles( album );
        }

        /**
         * {@inheritDoc}
         */
        public URI getResourceURI(Media media, Quality quality) {

            return getAlbumProviderService().getResourceURI( media, quality );
        }

        /**
         * {@inheritDoc}
         */
        public long modifiedTime(Media media) {

            return getAlbumProviderService().modifiedTime( media );
        }
    }
}
