package com.lyndir.lhunath.snaplog.data.service.impl.db4o;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.QueryEvaluationMode;
import com.db4o.ta.TransparentPersistenceSupport;
import com.google.inject.Provider;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3MediaData;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;


/**
 * <h2>{@link Db4oProvider}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 09, 2010</i> </p>
 *
 * @author lhunath
 */
public class Db4oProvider implements Provider<ObjectContainer> {

    final Collection<WeakReference<ObjectContainer>> containers = new LinkedList<WeakReference<ObjectContainer>>();

    @Override
    public ObjectContainer get() {

        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // TODO: Figure out why transparent persistence doesn't work with S3MediaData's map.
        configuration.common().add( new TransparentPersistenceSupport() );
        configuration.common().updateDepth( 5 );
        configuration.common().queries().evaluationMode( QueryEvaluationMode.LAZY );
        // TODO: Do this smarter; annotations or such.
        configuration.common().objectClass( User.class ).objectField( "userName" ).indexed( true );
        configuration.common().objectClass( User.class ).objectField( "linkID" ).indexed( true );
        configuration.common().objectClass( UserProfile.class ).objectField( "owner" ).indexed( true );
        configuration.common().objectClass( Source.class ).objectField( "name" ).indexed( true );
        configuration.common().objectClass( Source.class ).objectField( "ownerProfile" ).indexed( true );
        configuration.common().objectClass( Media.class ).objectField( "name" ).indexed( true );
        configuration.common().objectClass( S3Media.class ).objectField( "source" ).indexed( true );
        configuration.common().objectClass( S3MediaData.class ).objectField( "media" ).indexed( true );
        // TODO: NQ optimization isn't working.  Fix it or convert to SODA style or find a way to do better SODA through annotations.
        //        configuration.common().diagnostic().addListener( new DiagnosticListener() {
        //
        //            @Override
        //            public void onDiagnostic(final Diagnostic diagnostic) {
        //                logger.dbg( "[DB4O-DIAG] %s", diagnostic );
        //            }
        //        } );

        ObjectContainer db = Db4oEmbedded.openFile( configuration, "snaplog.db4o" );
        containers.add( new WeakReference<ObjectContainer>( db ) {

            @Override
            public void clear() {

                get().close();
                super.clear();
            }
        } );

        return db;
    }
}
