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

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


/**
 * <h2>{@link MediaFile}<br>
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
public class MediaFile implements Serializable, Comparable<MediaFile> {

    private static final DateTimeFormatter filenameFormat = ISODateTimeFormat.basicDateTimeNoMillis();

    private File                           file;


    public MediaFile(File file) {

        this.file = file;
    }

    /**
     * @return The on-disk file.
     */
    public File getFile() {

        return file;
    }

    public long shotTime() {

        String shotTimeString = file.getName();

        // Trim the extension off the filename.
        shotTimeString = shotTimeString.replaceFirst( "\\.[^\\.]*$", "" );

        // Trim the "hidden file prefix" off the filename.
        shotTimeString = shotTimeString.replaceFirst( "^\\.", "" );

        // No time zone == GMT.
        if (!shotTimeString.matches( "[+-]\\d+$" ))
            shotTimeString += "+0000";

        return filenameFormat.parseMillis( shotTimeString );
    }

    public long modifiedTime() {

        return file.lastModified();
    }

    public String getDateString() {

        return DateTimeFormat.longDate().withLocale( WebUtil.getLocale() ).print( shotTime() );
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(MediaFile o) {

        return (int) ((shotTime() - o.shotTime()) / 1000);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return getDateString() + ":" + shotTime();
    }
}
