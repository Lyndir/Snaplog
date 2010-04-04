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


/**
 * <h2>{@link UsernameTakenException}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 17, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class UsernameTakenException extends Exception {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private String userName;


    /**
     * @param userName The userName that is no longer available.
     */
    public UsernameTakenException(String userName) {

        this.userName = userName;
    }

    /**
     * @return The userName that is no longer available.
     */
    public String getUserName() {

        return userName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {

        return String.format( "Username '%s' is already taken.", userName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedMessage() {

        return msgs.message( userName );
    }


    interface Messages {

        String message(String userName);
    }
}
