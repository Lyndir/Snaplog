package com.lyndir.lhunath.snaplog.spike;

import com.lyndir.lhunath.lib.system.logging.Logger;


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

        Object[] arr = { "a", "b" };
        foo((Object)arr);
    }

    public static void foo(final Object... args) {

        logger.dbg( "%d", args.length );
        logger.dbg( "%s",args[0] );
    }
}
