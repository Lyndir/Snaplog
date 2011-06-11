package com.lyndir.lhunath.snaplog.data.service.impl.neodatis;

import com.google.inject.Provider;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.ILogger;


/**
 * <h2>{@link NeodatisProvider}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 09, 2010</i> </p>
 *
 * @author lhunath
 */
public class NeodatisProvider implements Provider<ODB> {

    static final Logger logger = Logger.get( NeodatisProvider.class );

    final Collection<WeakReference<ODB>> containers = new LinkedList<WeakReference<ODB>>();

    @Override
    public ODB get() {

        DLogger.register( new ILogger() {

            @Override
            public void debug(final Object o) {

                logger.dbg( "%s", o );
            }

            @Override
            public void info(final Object o) {

                logger.inf( "%s", o );
            }

            @Override
            public void error(final Object o) {

                logger.err( "%s", o );
            }

            @Override
            public void error(final Object o, final Throwable throwable) {

                logger.err( throwable, "%s", o );
            }
        } );

        ODB db = ODBFactory.open( "snaplog.odb" );
        containers.add( new WeakReference<ODB>( db ) {

            @Override
            public void clear() {

                get().close();
                super.clear();
            }
        } );

        return db;
    }
}
