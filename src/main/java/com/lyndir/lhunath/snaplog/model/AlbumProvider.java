package com.lyndir.lhunath.snaplog.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.AlbumData;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceInjector;


/**
 * <h2>{@link AlbumProvider}<br>
 * <sub>Enumeration of all supported Album providers.</sub></h2>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @param <A>
 *            The type of {@link Album} we provide services for.
 * @param <M>
 *            The type of {@link Media} that is available from A.
 * @author lhunath
 */
public class AlbumProvider<A extends Album, M extends Media> implements MediaProviderService<A, M> {

    private final Class<A> albumType;
    private final Class<? extends MediaProviderService<A, M>> mediaProviderServiceType;


    /**
     * @param albumType
     *            The type of albums that this provider can provide.
     * @param albumProviderServiceType
     *            The type of the {@link MediaProviderService} that services these types of albums.
     */
    public AlbumProvider(Class<A> albumType, Class<? extends MediaProviderService<A, M>> albumProviderServiceType) {

        this.albumType = checkNotNull( albumType );
        mediaProviderServiceType = checkNotNull( albumProviderServiceType );
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

        return GuiceInjector.get().getInstance( mediaProviderServiceType );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<M> getFiles(A album) {

        return getMediaProviderService().getFiles( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getResourceURI(M media, Quality quality) {

        return getMediaProviderService().getResourceURI( media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(M media) {

        return getMediaProviderService().modifiedTime( media );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlbumData newAlbumData(A album) {

        return getMediaProviderService().newAlbumData( album );
    }
}
