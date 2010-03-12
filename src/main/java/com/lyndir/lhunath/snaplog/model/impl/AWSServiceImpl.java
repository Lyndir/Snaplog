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
package com.lyndir.lhunath.snaplog.model.impl;

import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.AWSService;


/**
 * <h2>{@link AWSServiceImpl}<br>
 * 
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class AWSServiceImpl implements AWSService {

    private static final Logger logger = Logger.get( AWSServiceImpl.class );

    private static final String ERR_AWS_SERVICE = "Communication with the Amazon S3 services failed.";

    private static final S3Bucket BUCKET = new S3Bucket( "snaplog.net" );
    private static final String ACCESS_KEY = "AKIAJKXN44SDAGP7TINQ";
    private static final String SECRET_KEY = "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";


    private static S3Service newService()
            throws S3ServiceException {

        AWSCredentials awsCredentials = new AWSCredentials( ACCESS_KEY, SECRET_KEY );
        Jets3tProperties properties = Jets3tProperties.getInstance( Constants.JETS3T_PROPERTIES_FILENAME );
        properties.setProperty( "s3service.https-only", Boolean.FALSE.toString() );
        return new RestS3Service( awsCredentials, "Snaplog.net", null, properties );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Object readObject(String objectKey) {

        try {
            logger.dbg( "Fetching S3 object data from bucket: %s, with key: %s", BUCKET, objectKey );
            return newService().getObject( BUCKET, objectKey );
        }

        catch (S3ServiceException e) {
            throw logger.err( e, ERR_AWS_SERVICE ) //
                        .toError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Object findObjectDetails(String objectKey) {

        try {
            logger.dbg( "Fetching S3 object metadata from bucket: %s, with key: %s", BUCKET, objectKey );
            return newService().getObjectDetails( BUCKET, objectKey );
        }

        catch (S3ServiceException e) {
            if (e.getResponseCode() == 404)
                return null;

            throw logger.err( e, ERR_AWS_SERVICE ) //
                        .toError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<S3Object> listObjects(String objectKey) {

        try {
            logger.dbg( "Listing S3 objects from bucket: %s, with prefix: %s", BUCKET, objectKey );
            return ImmutableList.of( newService().listObjects( BUCKET, objectKey, null ) );
        }

        catch (S3ServiceException e) {
            throw logger.err( e, ERR_AWS_SERVICE ) //
                        .toError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Object upload(S3Object source) {

        try {
            logger.dbg( "Uploading: %d bytes, to S3 objects in bucket: %s, with prefix: %s", //
                        source.getContentLength(), BUCKET, source.getKey() );
            return newService().putObject( BUCKET, source );
        }

        catch (S3ServiceException e) {
            throw logger.err( e, ERR_AWS_SERVICE ) //
                        .toError();
        }
    }
}
