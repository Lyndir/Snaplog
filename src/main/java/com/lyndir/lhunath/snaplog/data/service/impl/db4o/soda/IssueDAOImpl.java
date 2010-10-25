package com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda;

import static com.google.common.base.Preconditions.*;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.object.Issue;
import com.lyndir.lhunath.snaplog.data.service.IssueDAO;
import java.util.List;


/**
 * <h2>{@link IssueDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class IssueDAOImpl implements IssueDAO {

    private final ObjectContainer db;

    @Inject
    public IssueDAOImpl(final ObjectContainer db) {

        this.db = db;
    }

    @Override
    public void update(final Issue issue) {

        db.store( issue );
    }

    @Override
    public Issue findIssue(final String issueCode) {

        checkNotNull( issueCode, "Given issueCode must not be null." );

        Query query = db.query();
        query.constrain( Issue.class ) //
                .and( query.descend( "issueCode" ).constrain( issueCode ) );

        ObjectSet<Issue> results = query.execute();
        if (results.hasNext()) {
            Issue result = results.next();
            checkState( !results.hasNext(), "Multiple issues found for code %s", issueCode );

            return result;
        }

        return null;
    }

    @Override
    public List<Issue> listIssues(final com.google.common.base.Predicate<Issue> predicate) {

        // TODO: Can't SODA this.  Maybe we should avoid this method altogether?
        return db.query( new Predicate<Issue>() {

            @Override
            public boolean match(final Issue candidate) {

                return predicate.apply( candidate );
            }
        } );
    }
}
