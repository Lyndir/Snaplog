package com.lyndir.lhunath.snaplog.spike;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.MediaData;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.model.impl.AWSMediaProviderServiceImpl;
import com.lyndir.lhunath.snaplog.model.impl.ServicesModule;


/**
 * <h2>{@link Db4oSpike}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 04, 2010</i> </p>
 *
 * @author lhunath
 */
public class Db4oSpike {

    private static Injector injector;
    private static ObjectContainer db;
    private static final String MEDIA_NAME = "20100418T162721.jpg";

    public static void main(final String... args)
            throws Exception {

        injector = Guice.createInjector( new ServicesModule() );
        db = injector.getInstance( ObjectContainer.class );

        Media media = getMedia();
        MediaData mediaData = getMediaData( media );
        System.err.println( "1. Media data: " + mediaData );

        injector.getInstance( AWSMediaProviderServiceImpl.class )
                .getResourceURL( SecurityToken.INTERNAL_USE_ONLY, (S3Media) media, Media.Quality.THUMBNAIL );

        mediaData = getMediaData( media );
        System.err.println( "2. Media data: " + mediaData );
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
