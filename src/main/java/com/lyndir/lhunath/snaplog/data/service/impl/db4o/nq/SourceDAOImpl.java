package com.lyndir.lhunath.snaplog.data.service.impl.db4o.nq;

import static com.google.common.base.Preconditions.checkNotNull;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.service.SourceDAO;
import java.util.List;


/**
 * <h2>{@link SourceDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class SourceDAOImpl implements SourceDAO {

    private final ObjectContainer db;

    @Inject
    public SourceDAOImpl(final ObjectContainer db) {

        this.db = db;
    }

    @Override
    public void update(final Source source) {

        db.store( source );
    }

    @Override
    public List<Source> listSources(final com.google.common.base.Predicate<Source> predicate) {

        checkNotNull( predicate, "Given predicate must not be null." );

        return db.query( new Predicate<Source>() {

            @Override
            public boolean match(final Source candidate) {

                return predicate.apply( candidate );
            }
        } );
    }

    @Override
    public List<Source> listSources() {

        return db.query( Source.class );
    }
}
