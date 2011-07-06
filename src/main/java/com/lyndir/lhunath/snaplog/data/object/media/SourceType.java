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

import com.google.common.base.Predicate;
import com.lyndir.lhunath.snaplog.security.SSecurityToken;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.object.media.aws.S3Source;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.model.service.AWSSourceService;
import com.lyndir.lhunath.snaplog.model.service.SourceService;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.net.URL;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * <h2>{@link SourceType}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 11, 2010</i> </p>
 *
 * @author lhunath
 */
public enum SourceType implements SourceService<Source, Media> {

    /**
     * Amazon S3.
     *
     * <p> Provides storage hosted at the Amazon cloud. </p>
     */
    AMAZON_S3( S3Source.class, AWSSourceService.class );

    private final Class<Source> sourceType;
    private final Class<? extends SourceService<Source, Media>> mediaProviderService;

    /**
     * @param sourceType           The type of media that is serviced by the mediaProviderService.
     * @param mediaProviderService The service that implements media provisioning services for the source type.
     */
    @SuppressWarnings({ "unchecked" })
    <S extends Source, M extends Media> SourceType(final Class<S> sourceType,
                                                   final Class<? extends SourceService<S, M>> mediaProviderService) {

        this.sourceType = (Class<Source>) sourceType;
        this.mediaProviderService = (Class<? extends SourceService<Source, Media>>) mediaProviderService;
    }

    private Class<Source> getSourceType() {

        return sourceType;
    }

    @Override
    public void loadMedia(final SSecurityToken token, final Source source)
            throws PermissionDeniedException {

        GuiceContext.getInstance( mediaProviderService ).loadMediaData( token, source );
    }

    @Override
    public void loadMediaData(final SSecurityToken token, final Source source)
            throws PermissionDeniedException {

        GuiceContext.getInstance( mediaProviderService ).loadMediaData( token, source );
    }

    @Override
    public Iterator<Source> iterateSources(final SSecurityToken token, final Predicate<Source> predicate) {

        return GuiceContext.getInstance( mediaProviderService ).iterateSources( token, predicate );
    }

    @Override
    public ListIterator<Media> iterateMedia(final SSecurityToken token, final Source source, final boolean ascending) {

        return GuiceContext.getInstance( mediaProviderService ).iterateMedia( token, source, ascending );
    }

    @Override
    public Media findMediaWithName(final SSecurityToken token, final User owner, final String mediaName) {

        return GuiceContext.getInstance( mediaProviderService ).findMediaWithName( token, owner, mediaName );
    }

    @Override
    public MediaMapping newMapping(final SSecurityToken token, final Media media)
            throws PermissionDeniedException {

        return GuiceContext.getInstance( mediaProviderService ).newMapping( token, media );
    }

    @Override
    public MediaMapping findMediaMapping(final SSecurityToken token, final String mapping) {

        return GuiceContext.getInstance( mediaProviderService ).findMediaMapping( token, mapping );
    }

    @Override
    public URL findResourceURL(final SSecurityToken token, final Media media, final Media.Quality quality)
            throws PermissionDeniedException {

        return GuiceContext.getInstance( mediaProviderService ).findResourceURL( token, media, quality );
    }

    @Override
    public void delete(final SSecurityToken token, final Media media)
            throws PermissionDeniedException {

        GuiceContext.getInstance( mediaProviderService ).delete( token, media );
    }

    @Override
    public Source newSource(final SSecurityToken token, final Source source) {

        return GuiceContext.getInstance( mediaProviderService ).newSource( token, source );
    }

    public static SourceType of(final Source source) {

        for (final SourceType sourceType : values())
            if (sourceType.getSourceType().isInstance( source ))
                return sourceType;

        throw new IllegalArgumentException( "No supported source type for: " + source );
    }

    public static SourceType of(final Class<Source> source) {

        for (final SourceType sourceType : values())
            if (sourceType.getSourceType().isAssignableFrom( source ))
                return sourceType;

        throw new IllegalArgumentException( "No supported source type for: " + source );
    }
}
