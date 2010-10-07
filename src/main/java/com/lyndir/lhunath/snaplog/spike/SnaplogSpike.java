package com.lyndir.lhunath.snaplog.spike;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.DateUtils;
import com.lyndir.lhunath.snaplog.data.DAOModule;
import com.lyndir.lhunath.snaplog.data.object.media.MediaData;
import com.lyndir.lhunath.snaplog.data.object.media.aws.*;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Source;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.SourceDAO;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.model.ServiceModule;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.io.File;


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

    public static void main(final String... args)
            throws Exception {

        GuiceContext.setInjector( injector = Guice.createInjector( new DAOModule(), new ServiceModule() ) );

        // foo();
        prepare();
        test();
    }

    private static void foo() {

        SnaplogConstants.DEFAULT_SOURCE = new S3Source( new UserProfile( new User( new LinkID( "linkid" ), "lhunath" ) ), "snaplog.net",
                                                       "users/lhunath/Life" );
        injector.getInstance( SourceDAO.class ).update( SnaplogConstants.DEFAULT_SOURCE );
        logger.inf( "Albums in db: %s", injector.getInstance( SourceDAO.class ).listSources() );
    }

    private static void test() {

        MediaDAO mediaDAO = injector.getInstance( MediaDAO.class );

        DateUtils.startTiming( "test" );
        try {
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_SOURCE, "1" ) );
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_SOURCE, "199998" ) );
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_SOURCE, "1" ) );
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_SOURCE, "199998" ) );
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    private static void prepare() {

        SnaplogConstants.DEFAULT_SOURCE = new S3Source( new UserProfile( new User( new LinkID( "linkid" ), "lhunath" ) ), "snaplog.net",
                                                       "users/lhunath/Life" );

        ImmutableList.Builder<MediaData<?>> mediaDatas = ImmutableList.builder();
        for (int i = 0; i < 20000; ++i) {
            logger.inf( "Creating media data #%d", i );
            mediaDatas.add( new S3MediaData( new S3Media( (S3Source) SnaplogConstants.DEFAULT_SOURCE, Integer.toString( i ) ) ) );
        }

        new File( "snaplog.odb" ).delete();
        new File( "snaplog.db4o" ).delete();
        logger.inf( "Storing all media datas" );
        for (final MediaData<?> mediaData : mediaDatas.build()) {
            injector.getInstance( MediaDAO.class ).update( mediaData );
        }
    }
}
