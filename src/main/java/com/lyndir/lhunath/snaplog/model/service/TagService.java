package com.lyndir.lhunath.snaplog.model.service;

import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.snaplog.security.SnaplogST;
import com.lyndir.lhunath.opal.wayward.collection.IPredicate;
import com.lyndir.lhunath.opal.wayward.model.WicketInjected;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import java.util.Iterator;
import org.joda.time.DateTimeFieldType;


/**
 * <h2>{@link TagService}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>10 03, 2010</i> </p>
 *
 * @author lhunath
 */
public interface TagService extends WicketInjected {

    Iterator<Tag> iterateTags(SnaplogST token, IPredicate<Tag> predicate);

    Iterator<Media> iterateMedia(SnaplogST token, Tag tag, boolean ascending);

    Tag findTagWithName(SnaplogST token, User tagOwner, String tagName);

    /**
     * @param token     Request authentication token should authorize {@link Permission#VIEW} on the source's media to return.
     * @param tag    The tag fro which to load time frames.
     * @param frame     The width of each frame to generate.  The returned time frames will divide the source's media into MediaTimeFrames
     *                  of one frame using the media's shot time as the media's reference time.
     * @param ascending Whether the first media returned should be the earliest (<code>true</code>) or latest (<code>false</code>).
     *
     * @return An {@link Iterator} of time frames that hold the source's media in a chronological ordering.
     */
    Iterator<TimeFrame> iterateTimeFrames(SnaplogST token, Tag tag, DateTimeFieldType frame, boolean ascending);

    /**
     * @param token  Request authentication token should authorize {@link Permission#VIEW} on the media to return.
     * @param source The media to create time frames for.
     * @param frame  The width of each frame to generate.  The returned time frames will divide the media into time frames of
     *               one frame using the media's shot time as the media's reference time.
     *
     * @return An {@link Iterator} of time frames that hold the given media in a chronological ordering.
     */
    Iterator<TimeFrame> iterateTimeFrames(SnaplogST token, Iterator<Media> source, DateTimeFieldType frame);
}
