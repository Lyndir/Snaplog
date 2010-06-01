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
public enum Tab {

    /**
     * This is the initial tab that describes Snaplog.
     */
    ABOUT( new AboutTabPanel.AboutTab() ),

    /**
     * This tab provides an exposition of other people on Snaplog.
     */
    EXPO( new ExpoTabPanel.ExpoTab() ),

    /**
     * This tab provides a summary view of a user's account.
     */
    GALLERY( new GalleryTabPanel.GalleryTab() ),

    /**
     * This tab provides a way of browsing a specific album.
     */
    ALBUM( new AlbumTabPanel.AlbumTab() ),

    /**
     * Using this tab, users can configure their profile and account settings.
     */
    ADMINISTRATION( new AdministrationTabPanel.AdministrationTab() );

    private final SnaplogTab tab;


    /**
     * Create a new {@link Tab} instance.
     *
     * @param tab The implementation of this tab.
     */
    Tab(final SnaplogTab tab) {

        this.tab = tab;
    }

    /**
     * @return The {@link SnaplogTab} that describes the UI elements of this tab.
     */
    public SnaplogTab get() {

        return tab;
    }
}
