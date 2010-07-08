package com.lyndir.lhunath.snaplog.spike;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.impl.ServicesModule;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;


/**
 * <h2>{@link SnaplogSpike}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 04, 2010</i> </p>
 *
 * @author lhunath
 */
public class SnaplogSpike {

    static final Logger logger = Logger.get( SnaplogSpike.class );
    private static Injector injector;
    private static ObjectContainer db;

    public static void main(final String... args)
            throws Exception {

        GuiceContext.setInjector( injector = Guice.createInjector( new ServicesModule() ) );
        injector.getInstance( AlbumService.class )
                .findResourceURL( SecurityToken.INTERNAL_USE_ONLY, new S3Media( (S3Album) SnaplogConstants.DEFAULT_ALBUM, "20100605T103809.jpg" ),
                                 Media.Quality.ORIGINAL );
        injector.getInstance( AlbumService.class )
                .findResourceURL( SecurityToken.INTERNAL_USE_ONLY, new S3Media( (S3Album) SnaplogConstants.DEFAULT_ALBUM, "20100605T103809.jpg" ),
                                 Media.Quality.ORIGINAL );

        // Shut down the database.
        db = injector.getInstance( ObjectContainer.class );
        if (!db.ext().isClosed())
            db.commit();
        while (!db.close()) {
        }

        GuiceContext.setInjector( injector = Guice.createInjector( new ServicesModule() ) );
        injector.getInstance( AlbumService.class )
                .findResourceURL( SecurityToken.INTERNAL_USE_ONLY, new S3Media( (S3Album) SnaplogConstants.DEFAULT_ALBUM, "20100605T103809.jpg" ),
                                 Media.Quality.ORIGINAL );
        injector.getInstance( AlbumService.class )
                .findResourceURL( SecurityToken.INTERNAL_USE_ONLY, new S3Media( (S3Album) SnaplogConstants.DEFAULT_ALBUM, "20100605T103809.jpg" ),
                                 Media.Quality.ORIGINAL );

        // Shut down the database.
        db = injector.getInstance( ObjectContainer.class );
        if (!db.ext().isClosed())
            db.commit();
        while (!db.close()) {
        }
    }
}
