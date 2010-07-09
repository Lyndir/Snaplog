package com.lyndir.lhunath.snaplog.spike;

import com.db4o.ObjectContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.impl.ServicesModule;
import com.lyndir.lhunath.snaplog.util.SnaplogConstants;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.listener.InitContext;
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
    private static ObjectContainer db;

    public static void main(final String... args)
            throws Exception {

        new File( "snaplog.db4o" ).delete();
        GuiceContext.setInjector( injector = Guice.createInjector( new ServicesModule() ) );
        new InitContext().contextInitialized( null );

        injector.getInstance( AlbumService.class ).loadMedia( SnaplogConstants.DEFAULT_ALBUM );
    }
}
