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
package com.lyndir.lhunath.snaplog.data.object.media;

/**
 * <h2>{@link MediaData}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 6, 2010</i> </p>
 *
 * @author lhunath
 */
public interface MediaData<M extends Media> {

    /**
     * @return The media of this {@link MediaData}.
     */
    M getMedia();

    /**
     * Purge cached data for this media.
     */
    void purge();
}
