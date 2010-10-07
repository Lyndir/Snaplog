package com.lyndir.lhunath.snaplog.data.service;

import com.lyndir.lhunath.snaplog.data.object.media.*;
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

    <M extends Media> M findMedia(Source source, String mediaName);

    <D extends MediaData<M>, M extends Media> D findMediaData(M media);

    <M extends Media> List<M> listMedia(Source source, boolean ascending);

    <D extends MediaData<?>> List<D> listMediaData(Source source, boolean ascending);

    <M extends Media> void delete(Iterable<M> medias);

    MediaMapping newMapping(MediaMapping mapping);

    MediaMapping findMediaMapping(String mapping);
}
