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
package com.lyndir.lhunath.snaplog.webapp.tab;

import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link SnaplogTab}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Feb 28, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SnaplogTab<P extends Panel> extends ITab {

    /**
     * @return A list of tools that this tab contributes to the toolbar.
     */
    List<? extends SnaplogTool> listTools();

    /**
     * @return The string that identifies this tab when it's the zero'th argument in the fragment part of the URL.
     */
    String getFragment();

    /**
     * Obtain the tab fragment and state arguments that would restore the state of the given panel of this tab.
     *
     * @param panel The panel for this tab; guaranteed of the type that was returned from #getPanel(String).
     *
     * @return All fragment arguments, the first being the same as #getFragment, all subsequent in the order that #applyFragmentState(Panel,
     *         String...) would take them to restore the given panel's state in another session.
     */
    Iterable<String> getFragmentState(Panel panel);

    /**
     * Apply fragment state specific to this tab.
     *
     * @param panel     The panel for this tab; guaranteed of the type that was returned from #getPanel(String)
     * @param arguments An array of arguments passed in the fragment part of the URL.  Excludes the tab fragment.
     */
    void applyFragmentState(Panel panel, String... arguments);

    @Override
    P getPanel(final String panelId);

    Class<P> getPanelClass();
}
