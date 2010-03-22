/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.snaplog.messages;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.system.localization.UseKey;
import com.lyndir.lhunath.lib.system.localization.UseLocalizationProvider;
import com.lyndir.lhunath.snaplog.webapp.SnaplogWebApplication;
import com.lyndir.lhunath.snaplog.webapp.tab.AdministrationTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.AlbumTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.HomeTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.GalleryTabPanel;
import com.lyndir.lhunath.snaplog.webapp.tab.WorkbenchTabPanel;


/**
 * <h2>{@link Messages}<br>
 * <sub>Localization interface for messages in the {@link SnaplogWebApplication}.</sub></h2>
 * 
 * <p>
 * <i>Mar 29, 2009</i>
 * </p>
 * 
 * @author lhunath
 * @see LocalizerFactory
 */
@UseLocalizationProvider(WicketLocalizationProvider.class)
public interface Messages {

    /**
     * @return Text on the interface tab to activate the {@link HomeTabPanel}.
     */
    @UseKey
    String homeTab();

    /**
     * @return Text on the interface tab to activate the {@link GalleryTabPanel}.
     */
    @UseKey
    String galleryTab();

    /**
     * @return Text on the interface tab to activate the {@link AlbumTabPanel}.
     */
    @UseKey
    String albumTab();

    /**
     * @param numberOfPhotosInYear
     *            The amount of photos that exist in that year.
     * 
     * @return Text on the year component of the timeline view.
     */
    @UseKey
    String albumTimelineYearPhotos(int numberOfPhotosInYear);

    /**
     * @return Text on the interface tab to activate the {@link WorkbenchTabPanel}
     */
    @UseKey
    String workbenchTab();

    /**
     * @return Text on the interface tab to activate the {@link AdministrationTabPanel}
     */
    @UseKey
    String administrationTab();

    /**
     * @param albumOwnerBadge
     *            The badge character of the owner of the currently viewed album.
     * @param albumOwnerName
     *            The name of the owner of the currently viewed album.
     * 
     * @return Text that will go in the page's title.
     */
    @UseKey
    String pageTitle(char albumOwnerBadge, String albumOwnerName);

    /**
     * @param userBadge
     *            The badge character of the logged-in user.
     * @param userName
     *            The name of the logged-in user.
     * 
     * @return Welcoming text greeting the logged-in user.
     */
    @UseKey
    String userWelcome(char userBadge, String userName);

    /**
     * @param userBadge
     *            The badge of the user we guess is using the page.
     * @param userName
     *            The name of the user we guess is using the page.
     * 
     * @return Welcoming the user back. The user has not yet authenticated himself. The identification is just a guess.
     */
    @UseKey
    String userWelcomeBack(char userBadge, String userName);

    /**
     * @return The designation of a user who we can't identify.
     */
    @UseKey
    String userNameUnknown();

    /**
     * @param messageCount
     *            The amount of messages the user has.
     * 
     * @return Text indicating the user has messages (in singular).
     */
    @UseKey
    String userMessagesSingular(int messageCount);

    /**
     * @param messageCount
     *            The amount of messages the user has.
     * 
     * @return Text indicating the user has messages (in plural).
     */
    @UseKey
    String userMessagesPlural(int messageCount);

    /**
     * @param requestCount
     *            The amount of pending requests.
     * 
     * @return Text indicating there are pending requests for the active user (in singular).
     */
    @UseKey
    String userRequestsSingular(int requestCount);

    /**
     * @param requestCount
     *            The amount of pending requests.
     * 
     * @return Text indicating there are pending requests for the active user (in plural).
     */
    @UseKey
    String userRequestsPlural(int requestCount);
}
