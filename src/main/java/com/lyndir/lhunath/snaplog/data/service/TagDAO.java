package com.lyndir.lhunath.snaplog.data.service;

import com.google.common.base.Predicate;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Tag;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import java.util.List;


/**
 * <h2>{@link TagDAO}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public interface TagDAO {

    void update(Tag tag);

    List<Tag> listTags(Predicate<Tag> predicate);

    List<Media> listMedia(Tag tag, boolean ascending);

    Tag findTag(User tagOwner, String tagName);
}
