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
package com.lyndir.lhunath.snaplog.webapp.tool;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link SnaplogTool}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Feb 28, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SnaplogTool {

    /**
     * @return The title on the toolbar for this tool.
     */
    IModel<String> getTitle();

    /**
     * @return The CSS class to apply to the title.  Useful for setting an icon or so.
     */
    IModel<String> getTitleClass();

    /**
     * @param id The wicket ID that the panel should bind to.
     *
     * @return The panel that provides the tool's features.
     */
    Panel getPanel(final String id);

    /**
     * @return <code>true</code> when this tool should be available from the toolbar.
     */
    boolean isVisible();
}
