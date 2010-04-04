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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <h2>{@link URLUtils}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * [description / usage].
 * </p>
 *
 * <p>
 * <i>Jan 19, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public abstract class URLUtils {

    private static final Logger logger = Logger.get( URLUtils.class );


    /**
     * Encode the given string for use in a URL query parameter.
     *
     * @param str The string that should be encoded.
     *
     * @return A string that is safe for use as a URL query parameter name or value.
     */
    public static String encode(String str) {

        try {
            return URLEncoder.encode( str, "UTF-8" );
        }
        catch (UnsupportedEncodingException e) {
            throw logger.bug( e ).toError();
        }
    }
}
