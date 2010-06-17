/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.webapp.listener;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.job.MediaSynchronizationJob;
import java.text.ParseException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;


/**
 * <h2>{@link QuartzContext}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Jan 11, 2010</i> </p>
 *
 * @author lhunath
 */
public class QuartzContext implements ServletContextListener {

    static final Logger logger = Logger.get( QuartzContext.class );

    @Override
    public void contextInitialized(final ServletContextEvent sce) {

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();
            scheduler.setJobFactory( new GuiceJobFactory( sce.getServletContext() ) );
            scheduler.scheduleJob( new JobDetail( "Media Synchronization Job", MediaSynchronizationJob.class ),
                                   new CronTrigger( "Media Synchronization Trigger: Daily", null, "0 0 3 * * ?" ) );
            logger.inf( "Quartz scheduler initialization completed." );
        }

        catch (SchedulerException e) {
            logger.err( e, "While initializing the scheduler." );
        }
        catch (ParseException e) {
            logger.bug( e, "While initializing the scheduler." );
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.shutdown();
        }

        catch (SchedulerException e) {
            logger.err( e, "While initializing the scheduler." );
        }
    }

    static class GuiceJobFactory implements JobFactory {

        private final ServletContext servletContext;

        @Inject
        GuiceJobFactory(final ServletContext servletContext) {

            this.servletContext = servletContext;
        }

        @Override
        public Job newJob(final TriggerFiredBundle bundle)
                throws SchedulerException {

            @SuppressWarnings({ "unchecked" })
            Class<Job> jobClass = bundle.getJobDetail().getJobClass();

            return GuiceContext.get( servletContext ).getInstance( jobClass );
        }
    }
}
