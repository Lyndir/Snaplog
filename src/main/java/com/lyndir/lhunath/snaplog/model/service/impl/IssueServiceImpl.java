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
package com.lyndir.lhunath.snaplog.model.service.impl;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.snaplog.security.SnaplogST;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.opal.security.service.SecurityService;
import com.lyndir.lhunath.snaplog.data.DAOModule;
import com.lyndir.lhunath.snaplog.data.object.Issue;
import com.lyndir.lhunath.snaplog.data.service.IssueDAO;
import com.lyndir.lhunath.snaplog.error.IssueNotFoundException;
import com.lyndir.lhunath.snaplog.model.ServiceModule;
import com.lyndir.lhunath.snaplog.model.service.IssueService;
import java.util.ListIterator;


/**
 * <h2>{@link IssueServiceImpl}<br>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public class IssueServiceImpl implements IssueService {

    private final IssueDAO issueDAO;
    private final SecurityService securityService;

    /**
     * @param issueDAO        See {@link DAOModule}.
     * @param securityService See {@link ServiceModule}.
     */
    @Inject
    public IssueServiceImpl(final IssueDAO issueDAO, final SecurityService securityService) {

        this.issueDAO = issueDAO;
        this.securityService = securityService;
    }

    @Override
    public void report(final Issue issue) {

        issueDAO.update( issue );
    }

    @Override
    public ListIterator<Issue> iterateIssues(final SnaplogST token, final Predicate<Issue> predicate) {

        return securityService.filterAccess( Permission.VIEW, token, issueDAO.listIssues( predicate ).listIterator() );
    }

    @Override
    public Issue getIssue(final SnaplogST token, final String issueCode)
            throws PermissionDeniedException, IssueNotFoundException {

        Issue issue = issueDAO.findIssue( issueCode );
        if (issue == null)
            throw new IssueNotFoundException( issueCode );

        return securityService.assertAccess( Permission.VIEW, token, issue );
    }
}
