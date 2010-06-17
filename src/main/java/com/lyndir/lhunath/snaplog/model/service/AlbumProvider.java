package com.lyndir.lhunath.snaplog.model.service;

import static com.google.common.base.Preconditions.checkNotNull;

import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.net.URL;


/**
 * <h2>{@link AlbumProvider}<br> <sub>Enumeration of all supported Album providers.</sub></h2>
 *
 * <p> <i>Jul 25, 2009</i> </p>
 *
 * @author lhunath
 * @param <A> The type of {@link Album} we provide services for.
 * @param <M> The type of {@link Media} that is available from A.
 */
public class AlbumProvider<A extends Album, M extends Media> implements MediaProviderService<A, M> {

    private final Class<A> albumType;
    private final Class<? extends MediaProviderService<A, M>> mediaProviderServiceType;

    /**
     * @param albumType                The type of albums that this provider can provide.
     * @param mediaProviderServiceType The type of the {@link MediaProviderService} that services these types of albums.
     */
    public AlbumProvider(final Class<A> albumType, final Class<? extends MediaProviderService<A, M>> mediaProviderServiceType) {

        this.albumType = checkNotNull( albumType, "Given album class must not be null." );
        this.mediaProviderServiceType = checkNotNull( mediaProviderServiceType, "Given media provider class must not be null." );
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

        return GuiceContext.getInstance( mediaProviderServiceType );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncMedia(final A album) {

        getMediaProviderService().syncMedia( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResourceURL(final SecurityToken token, final M media, final Quality quality)
            throws PermissionDeniedException {

        return getMediaProviderService().getResourceURL( token, media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(final SecurityToken token, final M media)
            throws PermissionDeniedException {

        return getMediaProviderService().modifiedTime( token, media );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public A newAlbum(final User ownerUser, final String albumName, final String albumDescription) {

        return getMediaProviderService().newAlbum( ownerUser, albumName, albumDescription );
    }
}
