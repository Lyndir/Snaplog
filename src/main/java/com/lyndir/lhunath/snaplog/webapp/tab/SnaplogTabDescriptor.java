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

import com.lyndir.lhunath.opal.wayward.navigation.TabDescriptor;
import com.lyndir.lhunath.opal.wayward.navigation.TabState;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link SnaplogTabDescriptor}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Feb 28, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SnaplogTabDescriptor<P extends Panel, S extends TabState<P>> extends TabDescriptor<P, S> {

    /**
     * @param panel The panel for which we need tools.  Use this to access the panel's state for the tools.
     *
     * @return A list of tools that this tab contributes to the toolbar.
     */
    List<? extends SnaplogTool> listTools(P panel);
}
