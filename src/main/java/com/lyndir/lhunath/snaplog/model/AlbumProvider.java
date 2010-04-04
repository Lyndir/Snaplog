package com.lyndir.lhunath.snaplog.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.Iterator;

import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.AlbumData;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;


/**
 * <h2>{@link AlbumProvider}<br>
 * <sub>Enumeration of all supported Album providers.</sub></h2>
 *
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 *
 * @author lhunath
 * @param <A>
 * The type of {@link Album} we provide services for.
 * @param <M>
 * The type of {@link Media} that is available from A.
 */
public class AlbumProvider<A extends Album, M extends Media> implements MediaProviderService<A, M> {

    private final Class<A> albumType;
    private final Class<? extends MediaProviderService<A, M>> mediaProviderServiceType;


    /**
     * @param albumType                The type of albums that this provider can provide.
     * @param albumProviderServiceType The type of the {@link MediaProviderService} that services these types of albums.
     */
    public AlbumProvider(Class<A> albumType, Class<? extends MediaProviderService<A, M>> albumProviderServiceType) {

        this.albumType = checkNotNull( albumType, "Given album class must not be null." );
        mediaProviderServiceType = checkNotNull( albumProviderServiceType,
                                                 "Given album provider class must not be null." );
    }

    /**
     * @return The albumType of this {@link AlbumProvider}.
     */
    public Class<? extends Album> getAlbumType() {

        return albumType;
    }

    /**
     * @return The albumProviderService of this {@link AlbumProvider}.
     */
    public MediaProviderService<A, M> getMediaProviderService() {

        return GuiceContext.get().getInstance( mediaProviderServiceType );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<M> iterateFiles(SecurityToken token, A album) {

        return getMediaProviderService().iterateFiles( token, album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResourceURL(SecurityToken token, M media, Quality quality)
            throws PermissionDeniedException {

        return getMediaProviderService().getResourceURL( token, media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(SecurityToken token, M media)
            throws PermissionDeniedException {

        return getMediaProviderService().modifiedTime( token, media );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlbumData newAlbumData(A album) {

        return getMediaProviderService().newAlbumData( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public A newAlbum(User ownerUser, String albumName, String albumDescription) {

        return getMediaProviderService().newAlbum( ownerUser, albumName, albumDescription );
    }
}
