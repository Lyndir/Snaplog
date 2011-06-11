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

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.wayward.model.EmptyModelProvider;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.tab.GalleryTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.HomeTabPanel.Messages;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link HomeTabModels}<br> <sub>Model provider for {@link GalleryTabPanel}.</sub></h2>
 *
 * <p> <i>Mar 11, 2010</i> </p>
 *
 * @author lhunath
 */
public class HomeTabModels extends EmptyModelProvider<HomeTabModels> {

    static final Logger logger = Logger.get( HomeTabModels.class );
    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final IModel<String> usersHelp;

    /**
     * Create a new {@link HomeTabModels} instance.
     */
    public HomeTabModels() {

        usersHelp = msgs.usersHelp( new LoadableDetachableModel<Boolean>() {

            @Override
            protected Boolean load() {

                return SnaplogSession.get().isAuthenticated();
            }
        } );
    }

    // Accessors.

    /**
     * @return A model providing an information string detailing the purpose of the users section.
     */
    public IModel<String> usersHelp() {

        return usersHelp;
    }
}
