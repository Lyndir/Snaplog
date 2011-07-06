package com.lyndir.lhunath.snaplog.security;

import com.lyndir.lhunath.opal.security.AbstractSecureObject;
import com.lyndir.lhunath.opal.security.SecureObject;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import org.jetbrains.annotations.NotNull;


/**
 * <i>07 07, 2011</i>
 *
 * @author lhunath
 */
public abstract class SSecureObject<P extends SecureObject<User, ?>> extends AbstractSecureObject<User, P> {

    protected SSecureObject() {

    }

    protected SSecureObject(@NotNull final User owner) {

        super( owner );
    }
}
