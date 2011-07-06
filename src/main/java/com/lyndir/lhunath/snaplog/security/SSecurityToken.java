package com.lyndir.lhunath.snaplog.security;

import com.lyndir.lhunath.opal.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;


/**
 * <i>07 07, 2011</i>
 *
 * @author lhunath
 */
public class SSecurityToken extends SecurityToken<User> {

    /**
     * Use this token <b>ONLY</b> for requests that the subject can't gain anything from. The result must not be given or hinted at to the
     * subject.
     */
    // TODO: Should this be moved into SecurityServiceImpl and made private?
    public static final SSecurityToken INTERNAL_USE_ONLY = new SSecurityToken( null ) {

        @Override
        public boolean isInternalUseOnly() {

            return true;
        }
    };

    /**
     * @param actor The subject that has requested or will gain access to the result of the operation.
     */
    public SSecurityToken(final User actor) {

        super( actor );
    }
}
