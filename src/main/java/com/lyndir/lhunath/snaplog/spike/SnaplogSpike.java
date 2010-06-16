package com.lyndir.lhunath.snaplog.spike;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.util.regex.Pattern;


/**
 * <h2>{@link SnaplogSpike}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 04, 2010</i> </p>
 *
 * @author lhunath
 */
public class SnaplogSpike {

    private static final String MEDIA_NAME = "20100418T162721.jpg";
    private static final Pattern VALID_NAME = Pattern.compile( "^.*\\.jpg" );
    private static final Pattern BASE_NAME = Pattern.compile( ".*/" );

    public static void main(final String... args)
            throws Exception {

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; ++i) {
//            VALID_NAME.matcher( MEDIA_NAME ).matches();
//            BASE_NAME.matcher( MEDIA_NAME ).replaceFirst( "" );
            MEDIA_NAME.endsWith( ".jpg" );
            Iterables.getLast( Splitter.on( '/' ).split( MEDIA_NAME ) );
        }
        System.out.println( "Duration: " + (System.currentTimeMillis() - start) + "ms" );
    }
}
