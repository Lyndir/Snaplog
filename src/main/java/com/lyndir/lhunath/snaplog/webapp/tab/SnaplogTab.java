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

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link SnaplogTab}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Feb 28, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public interface SnaplogTab extends ITab {

    /**
     * @param panelId The wicket ID that the panel should use.
     *
     * @return An optional panel that provides tools this tab contributes to the toolbar. <code>null</code> if this tab
     *         provides no tools.
     */
    public Panel getTools(String panelId);
}
