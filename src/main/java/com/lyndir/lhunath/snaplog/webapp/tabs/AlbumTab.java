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
package com.lyndir.lhunath.snaplog.webapp.tabs;

import java.util.Date;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.webapp.components.AccessView;
import com.lyndir.lhunath.snaplog.webapp.components.BrowserView;
import com.lyndir.lhunath.snaplog.webapp.components.TagsView;
import com.lyndir.lhunath.snaplog.webapp.components.TimelineView;


/**
 * <h2>{@link AlbumTab}<br>
 * <sub>The interface panel for browsing through the album content.</sub></h2>
 * 
 * <p>
 * <i>May 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class AlbumTab extends Panel {

    static final Logger logger  = Logger.get( AlbumTab.class );

    Messages            msgs    = LocalizerFactory.getLocalizer( Messages.class, this );

    // TODO: Unhardcode.
    static Date         current = new Date( 1259607804000l );


    public AlbumTab(String id) {

        super( id );

        // Browser
        add( new BrowserView( "browser", new Model<Date>( current ) ) );

        // Timeline.
        add( new TimelineView( "timelinePopup" ) );

        // Tags.
        add( new TagsView( "tagsPopup" ) );

        // Access.
        add( new AccessView( "accessPopup" ) );
    }
}
