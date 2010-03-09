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
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.AlbumData;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.data.MediaTimeFrame.Type;
import com.lyndir.lhunath.snaplog.model.AlbumProvider;
import com.lyndir.lhunath.snaplog.model.AlbumService;


/**
 * <h2>{@link AlbumServiceImpl}</h2>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that we provide album services for.
 * @author lhunath
 */
public class AlbumServiceImpl<P extends Provider> implements AlbumService<P> {

    private static final Logger logger = Logger.get( AlbumServiceImpl.class );

    ObjectContainer             db;


    @Inject
    public AlbumServiceImpl(ObjectContainer db) {

        this.db = db;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Album<P> findAlbumWithName(final User user, final String albumName) {

        checkNotNull( user );
        checkNotNull( albumName );

        return db.query( new Predicate<Album<P>>() {

            @Override
            public boolean match(Album<P> candidate) {

                return candidate.getUser().equals( user ) && candidate.getName().equals( albumName );
            }
        } ).next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Media<P> findMediaWithName(final Album<P> album, final String mediaName) {

        checkNotNull( album );
        checkNotNull( mediaName );

        ObjectSet<Media<P>> mediaQuery = db.query( new Predicate<Media<P>>() {

            @Override
            public boolean match(Media<P> candidate) {

                return candidate.getAlbum().equals( album ) && candidate.getName().endsWith( mediaName );
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
    public List<MediaTimeFrame<P>> getYears(Album<P> album) {

        checkNotNull( album );

        AlbumData<P> albumData = getAlbumData( album );
        ImmutableList<MediaTimeFrame<P>> timeFrames = albumData.getTimeFrames();
        if (timeFrames != null)
            return timeFrames;

        MediaTimeFrame<P> currentYear = null, currentMonth = null, currentDay = null;
        Builder<MediaTimeFrame<P>> timeFramesBuilder = new ImmutableList.Builder<MediaTimeFrame<P>>();

        for (Media<P> mediaFile : getFiles( album )) {
            long shotTime = mediaFile.shotTime();

            if (currentYear == null || !currentYear.containsTime( shotTime ))
                timeFramesBuilder.add( currentYear = new MediaTimeFrame<P>( null, Type.YEAR, shotTime ) );

            if (currentMonth == null || !currentMonth.containsTime( shotTime ))
                currentYear.addTimeFrame( currentMonth = new MediaTimeFrame<P>( currentYear, Type.MONTH, shotTime ) );

            if (currentDay == null || !currentDay.containsTime( shotTime ))
                currentMonth.addTimeFrame( currentDay = new MediaTimeFrame<P>( currentMonth, Type.DAY, shotTime ) );

            currentDay.addFile( mediaFile );
        }

        albumData.setTimeFrames( timeFrames = timeFramesBuilder.build() );
        db.store( albumData );

        return timeFrames;
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
    private AlbumData<P> getAlbumData(final Album<P> album) {

        checkNotNull( album );

        ObjectSet<AlbumData<P>> albumDataQuery = db.query( new Predicate<AlbumData<P>>() {

            @Override
            public boolean match(AlbumData<P> candidate) {

                return candidate.getAlbum().equals( album );
            }
        } );
        if (albumDataQuery.hasNext())
            return albumDataQuery.next();

        // No AlbumData yet for this album.
        AlbumData<P> albumData = new AlbumData<P>( album );
        db.store( albumData );

        return albumData;
    }

    private static <P extends Provider> AlbumProvider<P, Album<P>, Media<P>> getAlbumProvider(Album<P> album) {

        checkNotNull( album );

        @SuppressWarnings("unchecked")
        AlbumProvider<P, Album<P>, Media<P>>[] checkedValues = (AlbumProvider<P, Album<P>, Media<P>>[]) AlbumProvider.values;
        for (AlbumProvider<P, Album<P>, Media<P>> albumProvider : checkedValues)
            if (albumProvider.getAlbumType().isAssignableFrom( album.getClass() ))
                return albumProvider;

        throw logger.err( "Could not find a provider for the album type: %s", album.getClass() ) //
                    .toError( IllegalArgumentException.class );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Media<P>> getFiles(Album<P> album) {

        checkNotNull( album );

        AlbumData<P> albumData = getAlbumData( album );
        ImmutableList<Media<P>> files = albumData.getFiles();
        if (files != null)
            return files;

        albumData.setFiles( files = getAlbumProvider( album ).getFiles( album ) );
        db.store( albumData );

        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getResourceURI(Media<P> media, Quality quality) {

        checkNotNull( media );
        checkNotNull( quality );

        return getAlbumProvider( media.getAlbum() ).getResourceURI( media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(Media<P> media) {

        checkNotNull( media );

        return getAlbumProvider( media.getAlbum() ).modifiedTime( media );
    }
}
