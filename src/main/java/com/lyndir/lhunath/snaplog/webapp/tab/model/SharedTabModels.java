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
package com.lyndir.lhunath.snaplog.webapp.tab.model;

import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link SharedTabModels}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 12, 2010</i> </p>
 *
 * @author lhunath
 */
public class SharedTabModels extends ModelProvider<SharedTabModels, MediaMapping> {

    /**
     * @param model The model providing the media to show.
     */
    public SharedTabModels(final IModel<MediaMapping> model) {

        super( model );
    }

    // Accessors.
}
