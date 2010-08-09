package com.lyndir.lhunath.snaplog.spike;

import com.lyndir.lhunath.lib.system.logging.Logger;
import java.util.GregorianCalendar;


/**
 * <h2>{@link TinySpike}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 04, 2010</i> </p>
 *
 * @author lhunath
 */
public class TinySpike {

    static final Logger logger = Logger.get( TinySpike.class );

    public static void main(final String... args)
            throws Exception {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setLenient( false );
        calendar.set( 2000, 1, 31 );
        logger.inf( "%s", calendar.getTime() );
    }
}
