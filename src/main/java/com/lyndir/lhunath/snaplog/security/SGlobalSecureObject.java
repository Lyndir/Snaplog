package com.lyndir.lhunath.snaplog.security;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.lyndir.lhunath.opal.security.*;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;


/**
 * <i>07 07, 2011</i>
 *
 * @author lhunath
 */
public class SGlobalSecureObject extends GlobalSecureObject<User> {

    /**
     * The default {@link SecureObject} that all top-level objects should use as their parent.
     */
    public static final transient SGlobalSecureObject DEFAULT;

    static {
        ObjectContainer db = GuiceContext.get().getInstance( ObjectContainer.class );
        ObjectSet<SGlobalSecureObject> firstQuery = db.query( SGlobalSecureObject.class );

        if (firstQuery.hasNext())
            DEFAULT = firstQuery.next();

        else
            DEFAULT = new SGlobalSecureObject();

        DEFAULT.getACL().setDefaultPermission( Permission.NONE );
        db.store( DEFAULT );
    }
}
