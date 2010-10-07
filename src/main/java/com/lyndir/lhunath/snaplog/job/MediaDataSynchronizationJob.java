package com.lyndir.lhunath.snaplog.job;

import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.model.service.SourceService;
import java.util.Iterator;
import org.quartz.*;


/**
 * <h2>{@link MediaDataSynchronizationJob}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 28, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaDataSynchronizationJob implements Job {

    private final Provider<SourceService<Source, Media>> sourceServiceProvider;

    @Inject
    MediaDataSynchronizationJob(final Provider<SourceService<Source, Media>> sourceServiceProvider) {

        this.sourceServiceProvider = sourceServiceProvider;
    }

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {

        SourceService<Source, Media> sourceService = sourceServiceProvider.get();

        Iterator<Source> sourcesIt = sourceService.iterateSources( SecurityToken.INTERNAL_USE_ONLY, Predicates.<Source>alwaysTrue() );
        while (sourcesIt.hasNext())
            sourceService.loadMedia( SecurityToken.INTERNAL_USE_ONLY, sourcesIt.next() );
    }
}
