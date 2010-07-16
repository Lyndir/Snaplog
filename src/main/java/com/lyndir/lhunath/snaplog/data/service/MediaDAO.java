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

    <M extends Media> M findMedia(final Album album, final String mediaName);

    <D extends MediaData<M>, M extends Media> D findMediaData(final M media);

    <M extends Media> List<M> listMedia(Album album, String mediaName, final boolean ascending);

    <M extends Media> List<M> listMedia(Album album, final boolean ascending);

    <D extends MediaData<?>> List<D> listMediaData(Album album, final boolean ascending);

    void delete(Iterable<Media> medias);
}
