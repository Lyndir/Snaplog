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
 * <h2>{@link IllegalOperationException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class IllegalOperationException extends Exception {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    /**
     * @param messageFormat The message that explains why the operation request was illegal.
     * @param messageArgs   Additional arguments to expand into the message as defined by messageFormat.
     */
    public IllegalOperationException(final String messageFormat, final Object... messageArgs) {

        this( null, messageFormat, messageArgs );
    }

    /**
     * @param cause         An optional exception that caused this one.
     * @param messageFormat The message that explains why permission was denied in String#format syntax.
     * @param messageArgs   Additional arguments to expand into the message as defined by messageFormat.
     */
    public IllegalOperationException(final Throwable cause, final String messageFormat, final Object... messageArgs) {

        super( String.format( messageFormat, messageArgs ), cause );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedMessage() {

        return msgs.message();
    }

    interface Messages {

        String message();
    }
}