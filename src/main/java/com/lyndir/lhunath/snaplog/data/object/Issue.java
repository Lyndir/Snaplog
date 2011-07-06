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
package com.lyndir.lhunath.snaplog.data.object;

import com.google.common.base.Charsets;
import com.lyndir.lhunath.opal.crypto.MessageDigests;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.StringUtils;
import com.lyndir.lhunath.opal.wayward.js.JSUtils;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import com.lyndir.lhunath.snaplog.security.SSecureObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import org.apache.wicket.Component;


/**
 * <h2>{@link Issue}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 3, 2010</i> </p>
 *
 * @author lhunath
 */
public class Issue extends SSecureObject<UserProfile> {

    static final Logger logger = Logger.get( Issue.class );

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final String      originPath;
    private final Exception   cause;
    private final String      issueHash;
    private final String      issueCode;
    private final UserProfile subject;

    /**
     * Create a new {@link Issue} instance.
     *
     * @param origin  The page that the issue occurred on.
     * @param cause   The exception that caused the issue.
     * @param subject The user that was authenticated when the issue occurred or <code>null</code> if no user was authenticated.
     */
    public Issue(final Component origin, final Exception cause, final UserProfile subject) {

        logger.dbg( "New issue caused by: %s", cause );

        // Dump the exception to a string.
        StringWriter causeStringWriter = new StringWriter();
        PrintWriter causeStringPrintWriter = new PrintWriter( causeStringWriter );
        try {
            if (cause != null) {
                causeStringPrintWriter.write( cause.toString() );
                cause.printStackTrace( causeStringPrintWriter );
            }
        }
        finally {
            causeStringPrintWriter.close();
        }

        originPath = origin == null? null: origin.getClassRelativePath();
        this.cause = cause;
        this.subject = subject;

        issueHash = StringUtils.encodeHex(
                MessageDigests.MD5.of(
                        JSUtils.toString( new Object[]{ originPath, causeStringWriter.toString() } ).getBytes(
                                Charsets.UTF_8 ) ) );
        issueCode = UUID.randomUUID().toString();
    }

    /**
     * @return The path to the component where the {@link Issue} originated.
     */
    public String getOriginPath() {

        return originPath;
    }

    /**
     * @return The cause of this {@link Issue}.
     */
    public Exception getCause() {

        return cause;
    }

    /**
     * @return A unique code that can be used to reference this issue.
     */
    public String getIssueCode() {

        return issueCode;
    }

    /**
     * @return A code that identifies all issues with the same cause.
     */
    public String getIssueHash() {

        return issueHash;
    }

    @Override
    public UserProfile getParent() {

        return subject;
    }

    @Override
    public String getLocalizedType() {

        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {

        return msgs.instance( cause );
    }

    interface Messages {

        /**
         * @return The name of this type.
         */
        String type();

        /**
         * @param cause The cause of the issue.
         *
         * @return A description of the issue using the given cause.
         */
        String instance(Exception cause);
    }
}
