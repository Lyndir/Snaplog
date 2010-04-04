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
package com.lyndir.lhunath.snaplog.model;

import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.media.aws.S3Media;


/**
 * <h2>{@link AWSMediaProviderService}<br>
 * <sub>Service {@link Media} hosted at Amazon's S3.</sub></h2>
 *
 * <p>
 * <i>Jan 10, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public interface AWSMediaProviderService extends MediaProviderService<S3Album, S3Media> {

}
