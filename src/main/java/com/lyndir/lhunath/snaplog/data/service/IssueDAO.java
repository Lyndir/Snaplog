package com.lyndir.lhunath.snaplog.data.service;

import com.google.common.base.Predicate;
import com.lyndir.lhunath.snaplog.data.object.Issue;
import java.util.List;


/**
 * <h2>{@link IssueDAO}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public interface IssueDAO {

    void update(Issue issue);

    List<Issue> listIssues(Predicate<Issue> predicate);

    Issue findIssue(String issueCode);
}
