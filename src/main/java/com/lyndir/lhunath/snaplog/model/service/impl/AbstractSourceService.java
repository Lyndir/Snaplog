package com.lyndir.lhunath.snaplog.model.service.impl;

import com.google.common.base.Predicate;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import com.lyndir.lhunath.snaplog.data.service.SourceDAO;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.model.service.SourceService;
import java.util.*;
import org.joda.time.Duration;


/**
 * <h2>{@link AbstractSourceService}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>09 30, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractSourceService<S extends Source, M extends Media> implements SourceService<S, M> {

    private static final Logger logger = Logger.get( AbstractSourceService.class );

    protected final MediaDAO mediaDAO;
    protected final SourceDAO sourceDAO;
    protected final SecurityService securityService;

    protected AbstractSourceService(final SecurityService securityService, final SourceDAO sourceDAO, final MediaDAO mediaDAO) {

        this.securityService = securityService;
        this.sourceDAO = sourceDAO;
        this.mediaDAO = mediaDAO;
    }

    @Override
    public S newSource(final SecurityToken token, final S source) {

        sourceDAO.update( source );

        return source;
    }

    @Override
    public Media findMediaWithName(final SecurityToken token, final User owner, final String mediaName) {

        for (final Source source : sourceDAO.listSources( new Predicate<Source>() {
            @Override
            public boolean apply(final Source input) {

                return ObjectUtils.equal( input.getOwner(), owner );
            }
        } )) {
            Media media = mediaDAO.findMedia( source, mediaName );
            if (media != null)
                if (securityService.hasAccess( Permission.VIEW, token, media ))
                    return media;
                else
                    break;
        }

        return null;
    }

    @Override
    public MediaMapping newMapping(final SecurityToken token, final M media)
            throws PermissionDeniedException {

        securityService.assertAccess( Permission.ADMINISTER, token, media );
        // TODO: Avoid hash collisions by checking whether a mapping already exists for this new mapping and creating a new one.
        MediaMapping mapping = mediaDAO.newMapping( new MediaMapping( token.getActor(), media, new Duration( 10 * 60 * 1000L /*ms*/ ) ) );

        return securityService.assertAccess( Permission.VIEW, token, mapping );
    }

    @Override
    public MediaMapping findMediaMapping(final SecurityToken token, final String mapping) {

        MediaMapping mediaMapping = mediaDAO.findMediaMapping( mapping );
        if (!securityService.hasAccess( Permission.VIEW, token, mediaMapping ))
            return null;

        return mediaMapping;
    }

    @Override
    public ListIterator<M> iterateMedia(final SecurityToken token, final S source, final boolean ascending) {

        List<M> medias = mediaDAO.listMedia( source, ascending );
        return securityService.filterAccess( Permission.VIEW, token, medias.listIterator() );
    }

    @Override
    public Iterator<Source> iterateSources(final SecurityToken token, final Predicate<Source> predicate) {

        return securityService.filterAccess( Permission.VIEW, token, sourceDAO.listSources( predicate ).iterator() );
    }

    @Override
    public void delete(final SecurityToken token, final M media)
            throws PermissionDeniedException {

        securityService.assertAccess( Permission.ADMINISTER, token, media );

        mediaDAO.delete( Collections.singleton( media ) );
    }
}
