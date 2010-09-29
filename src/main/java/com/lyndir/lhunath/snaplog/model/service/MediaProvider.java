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
 * <h2>{@link MediaProvider}<br> <sub>Enumeration of all supported Media providers.</sub></h2>
 *
 * <p> <i>Jul 25, 2009</i> </p>
 *
 * @param <A> The type of {@link Album} we provide services for.
 * @param <M> The type of {@link Media} that is available from A.
 *
 * @author lhunath
 */
public class MediaProvider<A extends Album, M extends Media> implements MediaProviderService<A, M> {

    private final Class<A> albumType;
    private final Class<? extends MediaProviderService<A, M>> mediaProviderServiceType;

    /**
     * @param albumType                The type of albums that this provider can provide.
     * @param mediaProviderServiceType The type of the {@link MediaProviderService} that services these types of albums.
     */
    public MediaProvider(final Class<A> albumType, final Class<? extends MediaProviderService<A, M>> mediaProviderServiceType) {

        this.albumType = checkNotNull( albumType, "Given album class must not be null." );
        this.mediaProviderServiceType = checkNotNull( mediaProviderServiceType, "Given media provider class must not be null." );
    }

    /**
     * @return The albumType of this {@link MediaProvider}.
     */
    public Class<? extends Album> getAlbumType() {

        return albumType;
    }

    /**
     * @return The albumProviderService of this {@link MediaProvider}.
     */
    public MediaProviderService<A, M> getMediaProviderService() {

        return GuiceContext.getInstance( mediaProviderServiceType );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMedia(final A album) {

        getMediaProviderService().loadMedia( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMediaData(final A album) {

        getMediaProviderService().loadMediaData( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL findResourceURL(final SecurityToken token, final M media, final Quality quality)
            throws PermissionDeniedException {

        return getMediaProviderService().findResourceURL( token, media, quality );
    }

    @Override
    public void delete(final SecurityToken token, final M media)
            throws PermissionDeniedException {

        getMediaProviderService().delete( token, media );
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
