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
package com.lyndir.lhunath.snaplog.util;

import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.LinkID;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.data.aws.S3Album;


/**
 * <h2>{@link SnaplogConstants}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Jan 10, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class SnaplogConstants {

    /**
     * TODO: Unhardcode
     */
    public static final Album DEFAULT_ALBUM;
    /**
     * TODO: Unhardcode
     */
    public static final User DEFAULT_USER;

    static {
        DEFAULT_USER = new User( new LinkID( "b21e33e2-b63e-4f06-8f52-84509883e1d1" ), "lhunath" );
        DEFAULT_ALBUM = new S3Album( DEFAULT_USER, "Life" );
    }
}
