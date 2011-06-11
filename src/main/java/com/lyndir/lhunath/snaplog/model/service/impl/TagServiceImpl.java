package com.lyndir.lhunath.snaplog.model.service.impl;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.DateUtils;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.wayward.collection.IPredicate;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.service.TagDAO;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.model.service.TagService;
import java.util.Iterator;
import org.joda.time.*;


/**
 * <h2>{@link TagServiceImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>10 04, 2010</i> </p>
 *
 * @author lhunath
 */
public class TagServiceImpl implements TagService {

    static final Logger logger = Logger.get( TagServiceImpl.class );

    private final TagDAO tagDAO;
    private final SecurityService securityService;

    @Inject
    public TagServiceImpl(final TagDAO tagDAO, final SecurityService securityService) {

        this.tagDAO = tagDAO;
        this.securityService = securityService;
    }

    @Override
    public Iterator<Tag> iterateTags(final SecurityToken token, final IPredicate<Tag> predicate) {

        return securityService.filterAccess( Permission.VIEW, token, tagDAO.listTags( predicate ).iterator() );
    }

    @Override
    public Iterator<Media> iterateMedia(final SecurityToken token, final Tag tag, final boolean ascending) {

        return securityService.filterAccess( Permission.VIEW, token, tagDAO.listMedia( tag, ascending ).iterator() );
    }

    @Override
    public Tag findTagWithName(final SecurityToken token, final User tagOwner, final String tagName) {

        try {
            return securityService.assertAccess( Permission.VIEW, token, tagDAO.findTag( tagOwner, tagName ) );
        }
        catch (PermissionDeniedException ignored) {
            return null;
        }
    }

    @Override
    public Iterator<TimeFrame> iterateTimeFrames(final SecurityToken token, final Tag tag, final DateTimeFieldType frame,
                                                 final boolean ascending) {

        return iterateTimeFrames( token, iterateMedia( token, tag, ascending ), frame );
    }

    @Override
    public Iterator<TimeFrame> iterateTimeFrames(final SecurityToken token, final Iterator<Media> source, final DateTimeFieldType frame) {

        return new AbstractIterator<TimeFrame>() {

            public Media lastMedia;

            @Override
            protected TimeFrame computeNext() {

                if (!source.hasNext())
                    return endOfData();

                ImmutableList.Builder<Media> frameMedia = ImmutableList.builder();
                if (lastMedia == null)
                    lastMedia = source.next();

                // The offset and range of the frame.
                ReadableInstant offset = DateUtils.truncate( lastMedia.shotTime(), frame );
                ReadablePeriod range = DateUtils.period( offset, offset.toInstant().toDateTime().property( frame ).addToCopy( 1 ), frame );
                logger.dbg( "Frame starting at %s, range %s, starting with: %s", offset, range, lastMedia );

                do {

                    frameMedia.add( lastMedia );
                    if (!source.hasNext())
                        break;
                    lastMedia = source.next();
                } // Continue to add this lastMedia to the current list of media while its shotTime truncates to the offset.
                while (ObjectUtils.isEqual( DateUtils.truncate( lastMedia.shotTime(), frame ), offset ));

                return new TimeFrame( offset, range, frameMedia.build() );
            }
        };
    }
}
