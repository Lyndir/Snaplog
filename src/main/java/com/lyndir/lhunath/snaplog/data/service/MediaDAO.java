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

    void update(Iterable<MediaData<?>> mediaDatas);

    <M extends Media> M findMedia(Album album, String mediaName);

    <D extends MediaData<M>, M extends Media> D findMediaData(M media);

    <M extends Media> List<M> listMedia(Album album, boolean ascending);

    <D extends MediaData<?>> List<D> listMediaData(Album album, boolean ascending);

    <M extends Media> void delete(Iterable<M> medias);
}
