package com.lyndir.lhunath.snaplog.webapp.component;

import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;


/**
 * <h2>{@link Sprite}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 07, 2010</i> </p>
 *
 * @author lhunath
 */
public class Sprite extends WebComponent {

    /**
     * @param id       The wicket component identifier.
     * @param size     The size of the sprite image to use.
     * @param category The name of the sprite image's category.
     * @param name     The name of the sprite image in its category.
     */
    public Sprite(final String id, final int size, final String category, final String name) {

        super( id );

        add( CSSClassAttributeAppender.of( String.format( "s%d", size ), category, name ) );
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {

        if ("img".equalsIgnoreCase( tag.getName() ))
            tag.put( "src", "images/resources/spacer.gif" );

        super.onComponentTag( tag );
    }
}
