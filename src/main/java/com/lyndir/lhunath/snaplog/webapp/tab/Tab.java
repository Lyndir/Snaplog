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

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.navigation.FragmentNavigationTab;
import com.lyndir.lhunath.lib.wayward.navigation.FragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link SnaplogTab}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Feb 28, 2010</i> </p>
 *
 * @author lhunath
 */
public enum Tab {

    /**
     * This is the initial tab that describes Snaplog.
     */
    ABOUT( AboutTabPanel.AboutTab.instance ),

    /**
     * This tab provides an exposition of other people on Snaplog.
     */
    EXPO( ExpoTabPanel.ExpoTab.instance ),

    /**
     * This tab provides a summary view of a user's account.
     */
    GALLERY( GalleryTabPanel.GalleryTab.instance ),

    /**
     * This tab provides a way of browsing a specific album.
     */
    ALBUM( AlbumTabPanel.AlbumTab.instance ),

    /**
     * Using this tab, users can configure their profile and account settings.
     */
    ADMINISTRATION( AdministrationTabPanel.AdministrationTab.instance ),

    /**
     * This tab is shown when the user requests an expired page.
     */
    EXPIRED( PageExpiredErrorPage.PageExpiredErrorTab.instance ) {
        @Override
        public boolean isVisible() {

            return false;
        }},

    /**
     * This tab is shown when the user tries to access a page or resource to which he has no access.
     */
    DENIED( AccessDeniedErrorPage.AccessDeniedErrorTab.instance ) {
        @Override
        public boolean isVisible() {

            return false;
        }},

    /**
     * This tab details an error that occurred in the application.
     */
    ERROR( InternalErrorPage.InternalErrorTab.instance ) {
        @Override
        public boolean isVisible() {

            return false;
        }};

    static final Logger logger = Logger.get( Tab.class );

    private final SnaplogTab<?, ?> tab;

    /**
     * Create a new {@link Tab} instance.
     *
     * @param tab The implementation of this tab.
     */
    <P extends Panel, S extends FragmentState> Tab(final SnaplogTab<P, S> tab) {

        this.tab = tab;
    }

    /**
     * @return The {@link SnaplogTab} that describes the UI elements of this tab.
     */
    @SuppressWarnings({ "unchecked" })
    public <P extends Panel, S extends FragmentState> SnaplogTab<P, S> get() {

        // Since enum instances can't be generified we need to infer the generic type from the return value.
        return (SnaplogTab<P, S>) tab;
    }

    /**
     * Activate this tab in the current page.
     */
    public void activateNew() {

        LayoutPage.getController().activateNewTab( get() );
    }

    /**
     * Activate this tab in the current page and apply the given state to it.
     *
     * @param state The state to apply on the tab's new panel.
     */
    public <P extends Panel, S extends FragmentState> void activateWithState(final S state) {

        try {
            SnaplogTab<P, S> snaplogTab = get();
            LayoutPage.getController().activateTabWithState( snaplogTab, state );
        }
        catch (IncompatibleStateException e) {
            throw logger.bug( e ).toError();
        }
    }

    /**
     * @return <code>true</code> if the tab should be visible as an option to navigate to in the tab bar.
     */
    public boolean isVisible() {

        return get().isVisible();
    }

    public static Tab of(final FragmentNavigationTab<?, ?> tab) {

        for (final Tab enumTab : values())
            if (enumTab.get().getClass().isInstance( tab ))
                return enumTab;

        throw logger.err( "No known tab provides: %s", tab ).toError( IllegalArgumentException.class );
    }
}
