package com.lyndir.lhunath.snaplog.webapp.component;

import com.lyndir.lhunath.opal.security.Permission;


/**
 * <i>07 07, 2011</i>
 *
 * @author lhunath
 */
public class Sprites {

    public static Sprite of(final Permission permission, final String id, final int size) {

        switch (permission) {

            case NONE:
                return new Sprite( id, size, "alnum", "minus-sign5" );
            case INHERIT:
                return new Sprite( id, size, "arrows", "last-arrow-down" );
            case VIEW:
                return new Sprite( id, size, "people", "eye" );
            case CONTRIBUTE:
                return new Sprite( id, size, "business", "pencil7-sc49" );
            case ADMINISTER:
                return new Sprite( id, size, "business", "toolset-sc44" );
        }

        throw new IllegalArgumentException( "Unsupported permission: " + permission );
    }
}
