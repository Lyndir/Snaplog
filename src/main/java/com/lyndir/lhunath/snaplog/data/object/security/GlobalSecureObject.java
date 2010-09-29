/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.data.object.security;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;


/**
 * <h2>{@link GlobalSecureObject}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class GlobalSecureObject extends AbstractSecureObject<SecureObject<?>> {

    /**
     * The default {@link SecureObject} that all top-level objects should use as their parent.
     */
    public static final transient GlobalSecureObject DEFAULT;

    static {
        ObjectContainer db = GuiceContext.get().getInstance( ObjectContainer.class );
        ObjectSet<GlobalSecureObject> firstQuery = db.query( GlobalSecureObject.class );

        if (firstQuery.hasNext())
            DEFAULT = firstQuery.next();

        else
            DEFAULT = new GlobalSecureObject();

        DEFAULT.getACL().setDefaultPermission( Permission.NONE );
        db.store( DEFAULT );
    }

    protected GlobalSecureObject() {

        super( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecureObject<?> getParent() {

        return null;
    }

    @Override
    public String typeDescription() {

        throw new UnsupportedOperationException( "This object should not be shown publicly." );
    }

    @Override
    public String objectDescription() {

        throw new UnsupportedOperationException( "This object should not be shown publicly." );
    }
}
