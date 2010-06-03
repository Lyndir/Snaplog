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
import com.lyndir.lhunath.lib.wayward.component.RedirectToPageException;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackagedTextTemplate;


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
     * Activate the given tab in the session and switch to it in the {@link LayoutPage}.
     *
     * @param tab    The tab to activate.
     * @param target Optional AJAX request target. If specified, the components that need to be reloaded to update the page appropriately
     *               will be added to the target.
     */
    public static void setActiveTab(final Tab tab, final AjaxRequestTarget target) {

        SnaplogSession.get().setActiveContent( null );
        SnaplogSession.get().setActiveTab( tab );

        if (!LayoutPage.class.isInstance( RequestCycle.get().getResponsePage() ))
            throw new RedirectToPageException( LayoutPage.class );

        if (target != null) {
            LayoutPage page = (LayoutPage) RequestCycle.get().getResponsePage();
            page.addTabComponents( target );
        }
    }

    /**
     * @return The tab that should be activated in the layout.
     *
     * @see SnaplogSession#getActiveTab()
     */
    public static Tab getActiveTab() {

        // Find the active tab.
        Tab activeTab = SnaplogSession.get().getActiveTab();
        if (activeTab == null)
            for (final Tab tab : Tab.values()) {
                if (tab.get().isVisible()) {
                    SnaplogSession.get().setActiveTab( activeTab = tab );
                    break;
                }
            }
        checkNotNull( activeTab, "Couldn't find any tab to activate." );

        return activeTab;
    }

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

        JavaScriptTemplate trackJS = new JavaScriptTemplate( new PackagedTextTemplate( LayoutPage.class, "trackPage.js" ) );

        return trackJS.asString( trackVariables );
    }
}
