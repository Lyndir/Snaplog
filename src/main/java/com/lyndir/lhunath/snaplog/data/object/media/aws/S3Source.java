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
package com.lyndir.lhunath.snaplog.data.object.media.aws;

import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;


/**
 * <h2>{@link S3Source}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Jan 10, 2010</i> </p>
 *
 * @author lhunath
 */
public class S3Source extends Source {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final String bucket;
    private final String prefix;

    /**
     * @param ownerProfile The profile of the user that owns this source.
     * @param bucket       The S3 bucket that holds the data for objects provided by this source.
     * @param prefix       The prefix determines the "location" within the S3 bucket where this source's objects reside.  It should
     *                     <b>NOT</b> be terminated by a delimitor.
     */
    public S3Source(final UserProfile ownerProfile, final String bucket, final String prefix) {

        super( ownerProfile );

        this.bucket = bucket;
        this.prefix = prefix;
    }

    public String getBucket() {

        return bucket;
    }

    public String getPrefix() {

        return prefix;
    }

    @Override
    public String objectDescription() {

        return msgs.description( bucket, prefix );
    }

    interface Messages {

        String description(String bucket, String prefix);
    }
}
