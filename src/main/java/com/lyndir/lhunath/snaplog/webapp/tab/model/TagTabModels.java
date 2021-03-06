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

import com.lyndir.lhunath.opal.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link TagTabModels}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 12, 2010</i> </p>
 *
 * @author lhunath
 */
public class TagTabModels extends ModelProvider<TagTabModels, Tag> {

    private final IModel<Media> focusedMedia;

    /**
     * @param model The model providing the tag to show.
     */
    public TagTabModels(final IModel<Tag> model) {

        super( model );

        focusedMedia = new Model<Media>();
    }

    // Accessors.

    /**
     * @return A model that keeps track of the media the user is focusing on.
     */
    public IModel<Media> focusedMedia() {

        return focusedMedia;
    }
}
