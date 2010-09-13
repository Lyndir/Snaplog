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
package com.lyndir.lhunath.snaplog.model.service;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.wayward.model.WicketInjected;
import java.io.File;
import java.io.InputStream;
import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;


/**
 * <h2>{@link AWSService}<br> <sub>A service for clean access to the S3 data.</sub></h2>
 *
 * <p> <i>Jan 9, 2010</i> </p>
 *
 * @author lhunath
 */
public interface AWSService extends WicketInjected {

    /**
     * Retrieve an object on S3 with a data stream available.
     *
     * <p> <b>Note:</b> It is your responsibility to close this object's data stream when you are done! </p>
     *
     * @param objectKey The key that identifies the object.
     *
     * @return The object with metadata present.
     */
    S3Object readObject(String objectKey);

    /**
     * Look up metadata for an object on S3.
     *
     * @param objectKey The key that identifies the object.
     *
     * @return The object with metadata present or <code>null</code> if no object exists at the given key.
     */
    S3Object fetchObjectDetails(String objectKey);

    /**
     * List all objects under a certain key on S3.
     *
     * @param objectKey The key that identifies the object.
     *
     * @return A list of objects with minimal information present.
     *
     * @see S3Service#listObjects(S3Bucket)
     */
    ImmutableList<S3Object> listObjects(String objectKey);

    /**
     * Upload bytes to an S3 object at the given key.
     *
     * @param source The {@link S3Object} that provides the input data as an {@link InputStream} or {@link File}.
     *
     * @return The resulting object with metadata present.
     */
    S3Object upload(S3Object source);

    /**
     * Delete the S3 object at the given key from storage.
     *
     * @param objectKey The key that identifies the object.
     */
    void deleteObject(String objectKey);
}
