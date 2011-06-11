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
package com.lyndir.lhunath.snaplog.model.service;

import com.google.common.base.Predicate;
import com.lyndir.lhunath.opal.wayward.model.WicketInjected;
import com.lyndir.lhunath.snaplog.data.object.Issue;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.error.IssueNotFoundException;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import java.util.ListIterator;


/**
 * <h2>{@link IssueService}<br> <sub>Service to manage reporting and accessing {@link Issue}s.</sub></h2>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public interface IssueService extends WicketInjected {

    /**
     * Report a new issue that has just occurred.
     *
     * @param issue The issue that should be reported.
     */
    void report(Issue issue);

    /**
     * Retrieve all issues accessible using the given token that match the given predicate.
     *
     * @param token     The token should authorize VIEW permission on the desired issues.
     * @param predicate An optional predicate to filter the issues to return from the total set.
     *
     * @return All issues that the token grants VIEW permissions on and match the predicate if given.
     */
    ListIterator<Issue> iterateIssues(SecurityToken token, Predicate<Issue> predicate);

    /**
     * Retrieve a specific issue by its issue code.
     *
     * @param token     The token should authorize VIEW permission on the desired issue.
     * @param issueCode The unique code identifying the requested issue.
     *
     * @return The issue that has the given issue code.
     *
     * @throws PermissionDeniedException The given token does not authorize VIEW permission on the requested issue.
     * @throws IssueNotFoundException    No issue was found for the given issue code.
     */
    Issue getIssue(SecurityToken token, String issueCode)
            throws PermissionDeniedException, IssueNotFoundException;
}
