package com.lyndir.lhunath.snaplog.webapp.component;

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.AbstractReadOnlyModel;


/**
 * <h2>{@link EnumContextImage}<br> <sub>Show an image for a given enumeration value.</sub></h2>
 *
 * <p> <i>05 07, 2010</i> </p>
 *
 * @author lhunath
 */
public class EnumContextImage<E extends Enum<E>> extends ContextImage {

    /**
     * @param id          The wicket identifier to attach this component to.
     * @param contextPath The path inside the application context under which the enum's image is kept.
     * @param enumeration The enum value to obtain the image for.
     */
    public EnumContextImage(final String id, final String contextPath, final E enumeration) {

        super( id, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                return String.format( "%s/%s/%s", contextPath, enumeration.getDeclaringClass().getSimpleName(), enumeration.name() );
            }
        } );
    }

    public static <E extends Enum<E>> EnumContextImage<E> of(final String id, final String contextPath, final E enumeration) {

        return new EnumContextImage<E>( id, contextPath, enumeration );
    }
}
