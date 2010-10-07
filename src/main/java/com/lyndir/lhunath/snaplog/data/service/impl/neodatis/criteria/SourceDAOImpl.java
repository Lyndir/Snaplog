package com.lyndir.lhunath.snaplog.data.service.impl.neodatis.criteria;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Source;
import com.lyndir.lhunath.snaplog.data.service.SourceDAO;
import java.util.List;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;


/**
 * <h2>{@link SourceDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class SourceDAOImpl implements SourceDAO {

    // FIXME: Only querying for S3 implementations.  See <https://sourceforge.net/tracker/?func=detail&aid=3031161&group_id=179124&atid=887885>
    private final ODB db;

    @Inject
    public SourceDAOImpl(final ODB db) {

        this.db = db;
    }

    @Override
    public void update(final Source source) {

        db.store( source );
    }

    @Override
    public List<Source> listSources(final Predicate<Source> predicate) {

        checkNotNull( predicate, "Given predicate must not be null." );

        return ImmutableList.copyOf( Collections2.filter( listSources(), predicate ) );
    }

    @Override
    public List<Source> listSources() {

        Objects<Source> results = db.getObjects( S3Source.class );

        return ImmutableList.copyOf( results );
    }
}
