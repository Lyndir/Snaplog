package com.lyndir.lhunath.snaplog.spike;

import com.db4o.ObjectContainer;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.DateUtils;
import com.lyndir.lhunath.snaplog.data.object.media.MediaData;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3MediaData;
import com.lyndir.lhunath.snaplog.data.object.user.LinkID;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.model.service.impl.ServicesModule;
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

        GuiceContext.setInjector( injector = Guice.createInjector( new ServicesModule() ) );

        prepare();

        test();
    }

    private static void test() {

        MediaDAO mediaDAO = injector.getInstance( MediaDAO.class );

        DateUtils.startTiming( "test" );
        try {
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_ALBUM, "1" ) );
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_ALBUM, "199998" ) );
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_ALBUM, "1" ) );
            mediaDAO.findMediaData( mediaDAO.findMedia( SnaplogConstants.DEFAULT_ALBUM, "199998" ) );
        }
        finally {
            DateUtils.popTimer().logFinish();
        }
    }

    private static void prepare() {
        SnaplogConstants.DEFAULT_ALBUM = new S3Album( new UserProfile( new User( new LinkID( "linkid" ), "lhunath" ) ), "Life" );

        ImmutableList.Builder<MediaData<?>> mediaDatas = ImmutableList.builder();
        for (int i = 0; i < 20000; ++i) {
            logger.inf( "Creating media data #%d", i );
            mediaDatas.add( new S3MediaData( new S3Media( (S3Album) SnaplogConstants.DEFAULT_ALBUM, Integer.toString( i ) ) ) );
        }

        new File( "snaplog.db4o" ).delete();
        logger.inf( "Storing all media datas" );
        injector.getInstance( ObjectContainer.class ).store( mediaDatas );
    }
}
