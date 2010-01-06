/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.snaplog.model;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.MediaTimeFrame.Type;


/**
 * <h2>{@link MediaService}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class MediaService implements Serializable {

    private static final Logger               logger    = Logger.get( MediaService.class );

    private static final File                 originals = new File( "/home/lhunath/www/htdocs/album/.sized" );
    // private static final File originals = new File( "/Users/lhunath/Pictures/album/.sized" );

    private static LinkedList<MediaFile>      allFiles;
    private static LinkedList<MediaTimeFrame> timeFrames;


    public static Deque<MediaFile> getAllFiles() {

        if (!originals.isDirectory())
            throw logger.err( "Originals directory does not exist at %s.", originals ).toError();

        if (allFiles != null)
            // Cached files.
            return allFiles;

        allFiles = new LinkedList<MediaFile>();
        for (File file : originals.listFiles()) {
            if (!file.getName().endsWith( ".jpg" ))
                // Skip non-JPGs for now.
                continue;

            allFiles.add( new MediaFile( file ) );
        }

        logger.inf( "before: %s", allFiles );
        Collections.sort( allFiles );
        logger.inf( "after: %s", allFiles );
        return allFiles;
    }

    public static LinkedList<MediaTimeFrame> getTimeFrames() {

        if (timeFrames != null)
            return timeFrames;

        MediaTimeFrame currentYear = null, currentMonth = null, currentDay = null;
        timeFrames = new LinkedList<MediaTimeFrame>();

        for (MediaFile mediaFile : getAllFiles()) {
            long shotTime = mediaFile.shotTime();

            if (currentYear == null || !currentYear.containsTime( shotTime ))
                timeFrames.add( currentYear = new MediaTimeFrame( null, Type.YEAR, shotTime ) );

            if (currentMonth == null || !currentMonth.containsTime( shotTime ))
                currentYear.add( currentMonth = new MediaTimeFrame( currentYear, Type.MONTH, shotTime ) );

            if (currentDay == null || !currentDay.containsTime( shotTime ))
                currentMonth.add( currentDay = new MediaTimeFrame( currentMonth, Type.DAY, shotTime ) );

            currentDay.addFile( mediaFile );
        }

        return timeFrames;
    }
}
