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
package com.lyndir.lhunath.album.messages;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.system.localization.UseBundle;
import com.lyndir.lhunath.lib.system.localization.UseKey;


/**
 * <h2>{@link Messages}<br>
 * <sub>Localization interface for messages in the {@link UserApplication}.</sub></h2>
 * 
 * <p>
 * <i>Mar 29, 2009</i>
 * </p>
 * 
 * @see LocalizerFactory
 * 
 * @author lhunath
 */
@UseBundle(type = MessagesBundle.class)
public interface Messages {

    @UseKey
    String albumTitle(String albumOwnerBadge, String albumOwnerName);

    @UseKey
    String albumTab();

    @UseKey
    String albumTimelineYearPhotos(int numberOfPhotosInYear);

    @UseKey
    String workbenchTab();

    @UseKey
    String administrationTab();

    @UseKey
    String pageTitle(String albumOwnerName);

    @UseKey
    String userMessagesSingular(int messageCount);

    @UseKey
    String userMessagesPlural(int messageCount);

    @UseKey
    String userRequestsSingular(int requestCount);

    @UseKey
    String userRequestsPlural(int requestCount);
}
