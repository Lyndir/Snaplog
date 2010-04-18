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

import java.util.Date;

import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.media.Album;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link AlbumTabModels}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 12, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class AlbumTabModels extends ModelProvider<AlbumTabModels, Album> {

    private final IModel<Date> currentTime;


    /**
     * @param model The model providing the album to show.
     */
    public AlbumTabModels(final IModel<Album> model) {

        super( model );

        currentTime = new Model<Date>();
    }

    // Accessors.

    /**
     * @return A model that keeps track of the point in time of the album the user is focussed on.
     */
    public IModel<Date> currentTime() {

        return currentTime;
    }
}
