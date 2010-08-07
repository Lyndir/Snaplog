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
package com.lyndir.lhunath.snaplog.webapp.page.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;


/**
 * <h2>{@link LayoutPageUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 13, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class LayoutPageUtils {

    static final Logger logger = Logger.get( LayoutPageUtils.class );

    /**
     * Generate some JavaScript to track a user hit on the given component in the analytics tracker.
     *
     * @param trackComponent The component that we want to track a hit for.
     *
     * @return The JavaScript code that, when executed, will track the hit.
     */
    public static String trackJS(final Component trackComponent) {

        checkNotNull( trackComponent, "Given trackComponent must not be null." );

        Map<String, Object> trackVariables = new HashMap<String, Object>();
        trackVariables.put( "googleAnalyticsID", "UA-90535-10" ); // TODO: Unhardcode.
        trackVariables.put( "pageView", trackComponent.getClass().getSimpleName() );

        TextTemplate trackJS = new JavaScriptTemplate( new PackagedTextTemplate( LayoutPage.class, "trackPage.js" ) );
        return trackJS.asString( trackVariables );
    }
}
