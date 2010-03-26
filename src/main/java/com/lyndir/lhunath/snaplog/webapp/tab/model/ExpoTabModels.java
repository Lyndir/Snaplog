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

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.model.EmptyModelProvider;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tab.GalleryTabPanel;


/**
 * <h2>{@link ExpoTabModels}<br>
 * <sub>Model provider for {@link GalleryTabPanel}.</sub></h2>
 * 
 * <p>
 * <i>Mar 11, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class ExpoTabModels extends EmptyModelProvider<ExpoTabModels> {

    static final Logger logger = Logger.get( ExpoTabModels.class );

    private IModel<String> usersHelp;


    /**
     * Create a new {@link ExpoTabModels} instance.
     */
    public ExpoTabModels() {

        usersHelp = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return new StringResourceModel(
                        "usersHelp." + (SnaplogSession.get().isAuthenticated()? "auth": "anon"), getComponent(), null ).getObject();
            }
        };
    }

    // Accessors.

    /**
     * @return A model providing an information string detailing the purpose of the users section.
     */
    public IModel<String> usersHelp() {

        return usersHelp;
    }
}
