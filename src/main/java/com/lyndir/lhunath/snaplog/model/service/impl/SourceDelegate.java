package com.lyndir.lhunath.snaplog.model.service.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.snaplog.security.SSecurityToken;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.model.service.SourceService;
import java.net.URL;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * <h2>{@link SourceDelegate}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>10 24, 2010</i> </p>
 *
 * @author lhunath
 */
public class SourceDelegate implements SourceService<Source, Media> {

    @Override
    public void loadMedia(final SSecurityToken token, final Source source)
            throws PermissionDeniedException {

        SourceType.of( source ).loadMedia( token, source );
    }

    @Override
    public void loadMediaData(final SSecurityToken token, final Source source)
            throws PermissionDeniedException {

        SourceType.of( source ).loadMediaData( token, source );
    }

    @Override
    public Iterator<Source> iterateSources(final SSecurityToken token, final Predicate<Source> predicate) {

        return new AbstractIterator<Source>() {

            Iterator<SourceType> sourceTypeIt = Iterators.forArray( SourceType.values() );
            Iterator<Source> sourceIt;

            @Override
            protected Source computeNext() {

                while (sourceIt == null || !sourceIt.hasNext()) {
                    if (!sourceTypeIt.hasNext())
                        return endOfData();

                    sourceIt = sourceTypeIt.next().iterateSources( token, predicate );
                }

                return sourceIt.next();
            }
        };
    }

    @Override
    public ListIterator<Media> iterateMedia(final SSecurityToken token, final Source source, final boolean ascending) {

        return SourceType.of( source ).iterateMedia( token, source, ascending );
    }

    @Override
    public Media findMediaWithName(final SSecurityToken token, final User owner, final String mediaName) {

        for (final SourceType sourceType : SourceType.values()) {
            Media media = sourceType.findMediaWithName( token, owner, mediaName );
            if (media != null)
                return media;
        }

        return null;
    }

    @Override
    public MediaMapping newMapping(final SSecurityToken token, final Media media)
            throws PermissionDeniedException {

        return SourceType.of( media.getSource() ).newMapping( token, media );
    }

    @Override
    public MediaMapping findMediaMapping(final SSecurityToken token, final String mapping) {

        for (final SourceType sourceType : SourceType.values()) {
            MediaMapping mediaMapping = sourceType.findMediaMapping( token, mapping );
            if (mediaMapping != null)
                return mediaMapping;
        }

        return null;
    }

    @Override
    public URL findResourceURL(final SSecurityToken token, final Media media, final Media.Quality quality)
            throws PermissionDeniedException {

        return SourceType.of( media.getSource() ).findResourceURL( token, media, quality );
    }

    @Override
    public void delete(final SSecurityToken token, final Media media)
            throws PermissionDeniedException {

        SourceType.of( media.getSource() ).delete( token, media );
    }

    @Override
    public Source newSource(final SSecurityToken token, final Source source) {

        return SourceType.of( source ).newSource( token, source );
    }
}
