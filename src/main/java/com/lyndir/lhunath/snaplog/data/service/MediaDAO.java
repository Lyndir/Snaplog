package com.lyndir.lhunath.snaplog.data.service;

import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.MediaData;
import java.util.List;


/**
 * <h2>{@link MediaDAO}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public interface MediaDAO {

    void update(Media media);

    void update(MediaData<?> mediaData);

    <D extends MediaData<?>> D findMediaData(Album album, String mediaName);

    List<Media> listMedia(Album album, String mediaName);

    List<Media> listMedia(Album album);
}
