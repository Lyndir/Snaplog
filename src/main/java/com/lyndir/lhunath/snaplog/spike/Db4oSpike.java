package com.lyndir.lhunath.snaplog.spike;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4o.ta.TransparentPersistenceSupport;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * <h2>{@link Db4oSpike}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 06, 2010</i> </p>
 *
 * @author lhunath
 */
public class Db4oSpike {

    static final Logger logger = Logger.get( Db4oSpike.class );

    private static final String DB = "spike.db4o";

    public static void main(final String... args)
            throws Exception {

        EmbeddedObjectContainer db;
        ObjectSet<A> r;
        A a;

        // Delete the database.
        logger.inf( "Deleting db: %s", DB );
        new File( DB ).delete();

        // Store empty A & close.
        logger.inf( "Opening db: %s, storing empty A", DB );
        db = openDB( DB );
        a = new A( null );
        db.store( a );
        logger.inf( "Closing db: %s, done with A: %s", DB, a );
        db.close();

        // Extend A & close.
        logger.inf( "Opening db: %s, extending A", DB );
        db = openDB( DB );
        r = db.query( A.class );
        r.next().setB( new B( new C() ) );
        logger.inf( "Closing db: %s, done with A: %s", DB, a );
        db.close();

        // Find A & close.
        logger.inf( "Opening db: %s, checking A", DB );
        db = openDB( DB );
        r = db.query( A.class );
        logger.inf( "Closing db: %s, done with A: %s", DB, r.next() );
        db.close();
    }

    private static EmbeddedObjectContainer openDB(final String db) {

        // DB config.
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add( new TransparentPersistenceSupport() );
        //        configuration.common().activationDepth( 5 );
        //        configuration.common().updateDepth( 5 );
        configuration.common().reflectWith( new JdkReflector( Db4oSpike.class.getClassLoader() ) );
        configuration.common().diagnostic().addListener( new DiagnosticListener() {

            @Override
            public void onDiagnostic(final Diagnostic diagnostic) {
                System.out.println( diagnostic.toString() );
            }
        } );

        return Db4oEmbedded.openFile( configuration, db );
    }

    static class A {

        final Map<B, B> b;

        A(final B b) {
            this.b = new LinkedHashMap<B, B>();
            this.b.put( b, b );
        }

        public B getB() {
            return b.keySet().iterator().next();
        }

        public void setB(final B b) {
            this.b.put( b, b );
        }

        @Override
        public String toString() {

            return String.format( "{a: b=%s}", b.keySet().iterator().next() );
        }
    }


    static class B {

        C c;

        B(final C c) {
            this.c = c;
        }

        public C getC() {
            return c;
        }

        public void setC(final C c) {
            this.c = c;
        }

        @Override
        public String toString() {

            return String.format( "{b: c=%s}", c );
        }
    }


    static class C {

        @Override
        public String toString() {

            return String.format( "{c}" );
        }
    }
}
