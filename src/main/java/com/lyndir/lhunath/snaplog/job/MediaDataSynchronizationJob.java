package com.lyndir.lhunath.snaplog.job;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * <h2>{@link MediaDataSynchronizationJob}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 28, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaDataSynchronizationJob implements Job {

    private final Provider<AlbumService> albumServiceProvider;

    @Inject
    MediaDataSynchronizationJob(final Provider<AlbumService> albumServiceProvider) {

        this.albumServiceProvider = albumServiceProvider;
    }

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {

        albumServiceProvider.get().loadAllAlbumMediaData();
    }
}
