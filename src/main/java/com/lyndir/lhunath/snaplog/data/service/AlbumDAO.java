package com.lyndir.lhunath.snaplog.data.service;

import com.google.common.base.Predicate;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import java.util.List;
import org.joda.time.Duration;


/**
 * <h2>{@link AlbumDAO}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public interface AlbumDAO {

    void update(Album album);

    Album findAlbum(User ownerUser, String albumName);

    List<Album> listAlbums(Predicate<Album> predicate);

    List<Album> listAlbums();
}
