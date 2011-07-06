package com.lyndir.lhunath.snaplog.data.service.impl.neodatis.nq;

import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.security.SSecureObject;
import com.lyndir.lhunath.snaplog.data.service.SecurityDAO;
import org.neodatis.odb.ODB;


/**
 * <h2>{@link SecurityDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class SecurityDAOImpl implements SecurityDAO {

    private final ODB db;

    @Inject
    public SecurityDAOImpl(final ODB db) {

        this.db = db;
    }

    @Override
    public void update(final SSecureObject<?> secureObject) {

        db.store( secureObject );
    }
}
