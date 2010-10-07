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
package com.lyndir.lhunath.snaplog.error;

import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.user.User;


/**
 * <h2>{@link TagUnavailableException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 17, 2010</i> </p>
 *
 * @author lhunath
 */
public class TagUnavailableException extends Exception {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final User owner;
    private final String albumName;

    /**
     * @param owner     The owner of the album.
     * @param albumName The name of the album.
     */
    public TagUnavailableException(final User owner, final String albumName) {

        this.owner = owner;
        this.albumName = albumName;
    }

    public User getOwner() {

        return owner;
    }

    public String getAlbumName() {

        return albumName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {

        return String.format( "No permission or album not found: %s's %s.", getOwner(), getAlbumName() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedMessage() {

        return msgs.message( getOwner(), getAlbumName() );
    }

    interface Messages {

        String message(User owner, String albumName);
    }
}
