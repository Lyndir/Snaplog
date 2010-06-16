package com.lyndir.lhunath.snaplog.spike;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.MediaData;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.impl.AWSMediaProviderServiceImpl;
import com.lyndir.lhunath.snaplog.model.service.impl.ServicesModule;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;


/**
 * <h2>{@link SnaplogSpike}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 04, 2010</i> </p>
 *
 * @author lhunath
 */
public class SnaplogSpike {

    private static Injector injector;
    private static ObjectContainer db;
    private static final String MEDIA_NAME = "20100418T162721.jpg";

    public static void main(final String... args)
            throws Exception {

        GuiceContext.setInjector( injector = Guice.createInjector( new ServicesModule() ) );
        db = injector.getInstance( ObjectContainer.class );

        injector.getInstance( AlbumService.class ).syncAllAlbums();

        Media media = getMedia();
        MediaData mediaData = getMediaData( media );
        System.err.println( "1. Media data: " + mediaData );

        injector.getInstance( AWSMediaProviderServiceImpl.class )
                .getResourceURL( SecurityToken.INTERNAL_USE_ONLY, (S3Media) media, Media.Quality.THUMBNAIL );

        mediaData = getMediaData( media );
        System.err.println( "2. Media data: " + mediaData );

        mediaData.purge();
        db.store( mediaData );
    }

    private static Media getMedia() {
        ObjectSet<Media> mq = db.query( new Predicate<Media>() {
            @Override
            public boolean match(final Media o) {

                return o.getName().equals( MEDIA_NAME );
            }
        } );
        Preconditions.checkState( mq.hasNext(), "Media not found." );
        return mq.next();
    }

    private static MediaData getMediaData(final Media media) {

        ObjectSet<MediaData> mdq = db.query( new Predicate<MediaData>() {
            @Override
            public boolean match(final MediaData o) {

                return o.getMedia().equals( media );
            }
        } );
        Preconditions.checkState( mdq.hasNext(), "Media not found." );
        return mdq.next();
    }
}
