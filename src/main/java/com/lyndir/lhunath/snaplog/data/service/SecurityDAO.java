package com.lyndir.lhunath.snaplog.data.service;

import com.lyndir.lhunath.snaplog.security.SSecureObject;


/**
 * <h2>{@link SecurityDAO}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SecurityDAO {

    void update(SSecureObject<?> secureObject);
}
