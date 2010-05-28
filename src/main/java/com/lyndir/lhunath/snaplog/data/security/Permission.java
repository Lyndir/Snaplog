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
package com.lyndir.lhunath.snaplog.data.security;

import com.lyndir.lhunath.lib.wayward.i18n.KeyAppender;
import com.lyndir.lhunath.lib.wayward.i18n.Localized;
import com.lyndir.lhunath.lib.wayward.i18n.LocalizedType;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.webapp.component.Sprite;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * <h2>{@link Permission}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public enum Permission implements Localized {

    /**
     * This permission grants a {@link User} no access to the objects it applies to.
     *
     * <p> <b>NOTE:</b> This permission can't be provided. </p>
     */
    NONE( "alnum", "minus-sign5" ),

    /**
     * This causes the {@link User}'s permissions to be resolved against the parent of the objects it applies to.
     *
     * <p> <b>NOTE:</b> This permission can't be provided. </p>
     */
    INHERIT( "arrows", "last-arrow-down" ),

    /**
     * This permission grants a {@link User} the ability to read the objects it applies to.
     */
    VIEW( "people", "eye" ),

    /**
     * This permission grants a {@link User} the ability to modify the objects it applies to.
     */
    CONTRIBUTE( "business", "pencil7-sc49", VIEW ),

    /**
     * This permission grants a {@link User} the ability to manipulate the security constraints of the objects it applies to.
     */
    ADMINISTER( "business", "toolset-sc44", VIEW, CONTRIBUTE );

    private static final transient Messages msgs = MessagesFactory.create( Messages.class );

    private final String spriteName;
    private final String spriteCategory;
    private final Permission[] provided;

    Permission(final String spriteCategory, final String spriteName, final Permission... provided) {

        this.spriteCategory = spriteCategory;
        this.spriteName = spriteName;
        this.provided = provided;
    }

    /**
     * @return Other permissions provided (granted) by this one.
     */
    public Permission[] getProvided() {

        return provided;
    }

    /**
     * @param id   The wicket identifier of the new sprite component.
     * @param size The size of the sprite's image.
     *
     * @return A sprite that represents this permission state.
     */
    public Sprite newSprite(final String id, final int size) {

        return new Sprite( id, size, spriteName, spriteCategory );
    }

    @Override
    public String typeDescription() {

        return msgs.type();
    }

    @Override
    public String objectDescription() {

        return msgs.description( this );
    }

    /**
     * @param of The secureObject that this permission applies to.
     *
     * @return An explanation of the effects of this permission.
     */
    public String info(final SecureObject<?> of) {

        return msgs.info( this, of, of.getParent() );
    }

    private void readObject(final ObjectInputStream stream)
            throws IOException, ClassNotFoundException {

        // Default deserialization.
        stream.defaultReadObject();

        // Manually load a new Messages proxy.
        MessagesFactory.initialize( this, "msgs", Messages.class );
    }

    private interface Messages {

        /**
         * @return The name of this type.
         */
        String type();

        /**
         * @param permission The permission to explain.
         *
         * @return A description of the given permission.
         */
        String description(@KeyAppender Permission permission);

        /**
         * @param permission The permission to explain.
         * @param current    The object on which this permission applies.
         * @param parent     The parent object on which this permission applies.
         *
         * @return An information text explaining what this permission grants.
         */
        String info(@KeyAppender Permission permission, @LocalizedType SecureObject<?> current, SecureObject<?> parent);
    }
}
