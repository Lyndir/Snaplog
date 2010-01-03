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

import com.lyndir.lhunath.lib.system.localization.EnumResourceBundle;
import com.lyndir.lhunath.lib.system.localization.ValueEnum;


/**
 * <h2>{@link MessagesBundle}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Mar 29, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class MessagesBundle extends EnumResourceBundle<String> {

    /**
     * Create a new {@link MessagesBundle} instance.
     */
    public MessagesBundle() {

        super( MessagesBundleEnum.class );
    }


    static enum MessagesBundleEnum implements ValueEnum<String> {

        pageTitle("{0}{1}''s album"),
        credits("<b>Album</b> by"),

        welcome("Welcome"),
        userWelcome("Welcome {0}{1}"),
        userWelcomeBack("Welcome back {0}{1}"),
        userNameUnknown("stranger"),

        userLogin("Sign in"),
        userLogout("Sign out"),

        userMessagesSingular("{0} message"),
        userMessagesPlural("{0} messages"),
        userRequestsSingular("{0} request"),
        userRequestsPlural("{0} requests"),

        albumTab("Album"),
        albumTitle("{0}{1}''s album"),
        albumTimeline("Timeline"),
        albumTimelineInfo("The timeline gives you a quick way to jump through an album chronologically.<br /><br />"
                          + "Snaps are categorized into the years, months and days that they were taken.  "
                          + "Unfold a category by clicking on it to see the subcategories it holds.  "
                          + "Click on a day to jump to it in the album viewer."),
        albumTimelineYearPhotos("({0} photos)"),

        toolTimeline("Timeline"),
        toolTags("Tags"),
        toolAccess("Access"),

        workbenchTab("Workbench"),

        administrationTab("Administration");

        // Internal operation --

        private String value;


        private MessagesBundleEnum(String value) {

            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        public String value() {

            return value;
        }
    }
}
