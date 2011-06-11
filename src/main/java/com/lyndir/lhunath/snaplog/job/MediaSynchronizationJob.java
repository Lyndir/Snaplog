package com.lyndir.lhunath.snaplog.job;

import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.impl.SourceDelegate;
import java.util.Iterator;
import org.quartz.*;


/**
 * <h2>{@link MediaSynchronizationJob}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 28, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaSynchronizationJob implements Job {

    static final Logger logger = Logger.get( MediaSynchronizationJob.class );

    private final Provider<SourceDelegate> sourceDelegateProvider;

    @Inject
    MediaSynchronizationJob(final Provider<SourceDelegate> sourceDelegateProvider) {

        this.sourceDelegateProvider = sourceDelegateProvider;
    }

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {

        SourceDelegate sourceDelegate = sourceDelegateProvider.get();

        Iterator<Source> sourcesIt = sourceDelegate.iterateSources( SecurityToken.INTERNAL_USE_ONLY, Predicates.<Source>alwaysTrue() );
        while (sourcesIt.hasNext())
            try {
                sourceDelegate.loadMedia( SecurityToken.INTERNAL_USE_ONLY, sourcesIt.next() );
            }
            catch (PermissionDeniedException e) {
                logger.bug( e, "Job should run with %s which should have had sufficient permissions." , SecurityToken.INTERNAL_USE_ONLY );
            }
    }
}
