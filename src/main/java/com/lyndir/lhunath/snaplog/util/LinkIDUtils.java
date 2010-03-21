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
package com.lyndir.lhunath.snaplog.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.link.safeonline.sdk.auth.filter.LoginManager;

import org.apache.wicket.RequestCycle;

import com.lyndir.lhunath.lib.wayward.component.WicketUtils;


/**
 * <h2>{@link LinkIDUtils}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 21, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class LinkIDUtils {

    /**
     * Look up an attribute as returned by the linkID authentication response.
     * 
     * <p>
     * When the attribute has multiple attribute values, only the first is returned.
     * </p>
     * 
     * <p>
     * <b>NOTE:</b> Only works inside a Wicket {@link RequestCycle}.
     * </p>
     * 
     * @param attributeName
     *            The name of the linkID attribute to look up.
     * @param attributeClass
     *            The type of the attribute's value.
     * @param <T>
     *            See attributeClass
     * @return The first attribute value of attribute by the given name, or <code>null</code> if there are no attributes
     *         on the session, the given attribute is not on the session or there are no values for the given attribute.
     */
    public static <T> T findSingleAttribute(String attributeName, Class<T> attributeClass) {

        Map<String, Object> attributes = LoginManager.findAttributes( WicketUtils.getServletRequest() );
        if (attributes == null)
            return null;

        Object attributeValue = attributes.get( attributeName );
        if (attributeValue instanceof Iterable<?>) {
            @SuppressWarnings("unchecked")
            Iterator<T> attributeIt = ((List<T>) attributeValue).iterator();

            if (!attributeIt.hasNext())
                return null;

            return attributeIt.next();
        }

        return attributeClass.cast( attributeValue );
    }
}
