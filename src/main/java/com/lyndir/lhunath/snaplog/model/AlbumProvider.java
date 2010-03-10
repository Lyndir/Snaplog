package com.lyndir.lhunath.snaplog.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.data.aws.S3Album;
import com.lyndir.lhunath.snaplog.data.aws.S3Media;
import com.lyndir.lhunath.snaplog.data.aws.S3Provider;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceInjector;


/**
 * <h2>{@link AlbumProvider}<br>
 * <sub>Enumeration of all supported Album providers.</sub></h2>
 * 
 * <p>
 * <i>Jul 25, 2009</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} of the album we provide services for.
 * @param <A>
 *            The type of {@link Album} we provide services for.
 * @param <M>
 *            The type of {@link Media} that is available from A.
 * @author lhunath
 */
public class AlbumProvider<P extends Provider, A extends Album<P>, M extends Media<P>>
        implements MediaProviderService<P, A, M> {

    /**
     * Amazon S3.
     * 
     * <p>
     * Provides storage hosted at the Amazon cloud.
     * </p>
     */
    public static AlbumProvider<S3Provider, S3Album, S3Media>    AMAZON_S3 = new AlbumProvider<S3Provider, S3Album, S3Media>(
                                                                                   S3Album.class,
                                                                                   AWSMediaProviderService.class );

    public static AlbumProvider<?, ?, ?>                         values[]  = { AMAZON_S3 };

    private final Class<? extends Album<P>>                      albumType;
    private final Class<? extends MediaProviderService<P, A, M>> mediaProviderServiceType;


    private AlbumProvider(Class<? extends Album<P>> albumType,
                          Class<? extends MediaProviderService<P, A, M>> albumProviderServiceType) {

        this.albumType = checkNotNull( albumType );
        this.mediaProviderServiceType = checkNotNull( albumProviderServiceType );
    }

    /**
     * @return The albumType of this {@link AlbumProvider}.
     */
    public Class<? extends Album<P>> getAlbumType() {

        return albumType;
    }

    /**
     * @return The albumProviderService of this {@link AlbumProvider}.
     */
    public MediaProviderService<P, A, M> getMediaProviderService() {

        return GuiceInjector.get().getInstance( mediaProviderServiceType );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<M> getFiles(A album) {

        checkNotNull( album );

        return getMediaProviderService().getFiles( album );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getResourceURI(M media, Quality quality) {

        checkNotNull( media );
        checkNotNull( quality );

        return getMediaProviderService().getResourceURI( media, quality );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long modifiedTime(M media) {

        checkNotNull( media );

        return getMediaProviderService().modifiedTime( media );
    }
}
