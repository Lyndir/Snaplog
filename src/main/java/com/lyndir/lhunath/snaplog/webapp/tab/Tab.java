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


/**
 * <h2>{@link TabProvider}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Feb 28, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public enum Tab implements TabProvider {

    /**
     * This is the initial tab that describes Snaplog.
     */
    HOME( new HomeTab() ),

    /**
     * This tab provides a summary view of a user's account.
     */
    GALLERY( new GalleryTab() ),

    /**
     * This tab provides a way of browsing a specific album.
     */
    ALBUM( new AlbumTab() ),

    /**
     * This tab provides the tools to manipulate media.
     */
    WORKBENCH( new WorkbenchTab() ),

    /**
     * Using this tab, users can configure their profile and account settings.
     */
    ADMINISTRATION( new AdministrationTab() );

    private ITab tab;


    /**
     * Create a new {@link Tab} instance.
     */
    private Tab(ITab tab) {

        this.tab = tab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITab getTab() {

        return tab;
    }
}
