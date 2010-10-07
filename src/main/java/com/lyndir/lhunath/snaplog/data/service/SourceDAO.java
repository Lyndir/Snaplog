package com.lyndir.lhunath.snaplog.data.service;

import com.google.common.base.Predicate;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import java.util.List;


/**
 * <h2>{@link SourceDAO}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SourceDAO {

    void update(Source source);

    List<Source> listSources(Predicate<Source> predicate);

    List<Source> listSources();
}
