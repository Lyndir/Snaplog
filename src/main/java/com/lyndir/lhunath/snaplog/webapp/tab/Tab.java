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

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.navigation.*;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link SnaplogTabDescriptor}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Feb 28, 2010</i> </p>
 *
 * @author lhunath
 */
public enum Tab {

    /**
     * This tab provides a gateway to Snaplog's most needed functions and information.
     */
    HOME( HomeTabPanel.HomeTabDescriptor.instance ),

    /**
     * This tab describes what Snaplog is.
     */
    ABOUT( AboutTabPanel.AboutTabDescriptor.instance ),

    /**
     * This tab provides a summary view of a user's account.
     */
    GALLERY( GalleryTabPanel.GalleryTabDescriptor.instance ),

    /**
     * This tab provides a way of browsing a specific tag.
     */
    TAG( TagTabPanel.TagTabDescriptor.instance ),

    /**
     * This tab provides a way of viewing media mappings.
     */
    SHARED( SharedTabPanel.SharedTabDescriptor.instance ),

    /**
     * Using this tab, users can configure their profile and account settings.
     */
    ADMINISTRATION( AdministrationTabPanel.AdministrationTabDescriptor.instance ),

    /**
     * This tab is shown when the user requests an expired page.
     */
    EXPIRED( PageExpiredErrorPage.PageExpiredErrorTabDescriptor.instance ) {
        @Override
        public boolean isVisible() {

            return false;
        }
    },

    /**
     * This tab is shown when the user tries to access a page or resource to which he has no access.
     */
    DENIED( AccessDeniedErrorPage.AccessDeniedErrorTabDescriptor.instance ) {
        @Override
        public boolean isVisible() {

            return false;
        }
    },

    /**
     * This tab details an error that occurred in the application.
     */
    ERROR( InternalErrorPage.InternalErrorTabDescriptor.instance ) {
        @Override
        public boolean isVisible() {

            return false;
        }
    };

    static final Logger logger = Logger.get( Tab.class );

    private final SnaplogTabDescriptor<?, ?> tab;

    /**
     * @param tab The implementation of this tab.
     */
    <S extends TabState<P>, P extends Panel> Tab(final SnaplogTabDescriptor<P, S> tab) {

        this.tab = tab;
    }

    /**
     * @return The {@link SnaplogTabDescriptor} that describes the UI elements of this tab.
     */
    @SuppressWarnings({ "unchecked" })
    public SnaplogTabDescriptor<?, ?> get() {

        // Since enum instances can't be generified we need to infer the generic type from the return value.
        return tab;
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
    public <S extends TabState<P>, P extends Panel> void activateWithState(final S state) {

        try {
            @SuppressWarnings({ "unchecked" })
            SnaplogTabDescriptor<P, S> snaplogTab = (SnaplogTabDescriptor<P, S>) get();
            LayoutPage.getController().activateTabWithState( snaplogTab, state );
        }
        catch (IncompatibleStateException e) {
            throw logger.bug( e );
        }
    }

    /**
     * @return <code>true</code> if the tab should be visible as an option to navigate to in the tab bar.
     */
    public boolean isVisible() {

        return get().shownInNavigation();
    }

    public static Tab of(final TabDescriptor<?, ?> tab) {

        for (final Tab enumTab : values())
            if (enumTab.get().getClass().isInstance( tab ))
                return enumTab;

        throw new IllegalArgumentException( "No known tab provides: " + tab );
    }
}
