package com.lyndir.lhunath.snaplog.data.service.impl.neodatis;

import com.db4o.Db4oEmbedded;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.QueryEvaluationMode;
import com.db4o.ta.TransparentPersistenceSupport;
import com.google.inject.Provider;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3MediaData;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
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

        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // TODO: Figure out why transparent persistence doesn't work with S3MediaData's map.
        configuration.common().add( new TransparentPersistenceSupport() );
        configuration.common().updateDepth( 5 );
        configuration.common().queries().evaluationMode( QueryEvaluationMode.LAZY );
        // TODO: Do this smarter; annotations or such.
        configuration.common().objectClass( User.class ).objectField( "userName" ).indexed( true );
        configuration.common().objectClass( User.class ).objectField( "linkID" ).indexed( true );
        configuration.common().objectClass( UserProfile.class ).objectField( "user" ).indexed( true );
        configuration.common().objectClass( Album.class ).objectField( "name" ).indexed( true );
        configuration.common().objectClass( Album.class ).objectField( "ownerProfile" ).indexed( true );
        configuration.common().objectClass( Media.class ).objectField( "name" ).indexed( true );
        configuration.common().objectClass( S3Media.class ).objectField( "album" ).indexed( true );
        configuration.common().objectClass( S3MediaData.class ).objectField( "media" ).indexed( true );
        // TODO: NQ optimization isn't working.  Fix it or convert to SODA style or find a way to do better SODA through annotations.
        //        configuration.common().diagnostic().addListener( new DiagnosticListener() {
        //
        //            @Override
        //            public void onDiagnostic(final Diagnostic diagnostic) {
        //                logger.dbg( "[DB4O-DIAG] %s", diagnostic );
        //            }
        //        } );

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
