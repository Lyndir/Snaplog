package com.lyndir.lhunath.snaplog.data;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;


/**
 * <h2>{@link AlbumData}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Jan 28, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that provides the resources cached by this album data.
 * @param <A>
 *            The type of {@link Album} we cache data for.
 * @param <M>
 *            The type of {@link Media} that is available from A.
 * @author lhunath
 */
public class AlbumData<P extends Provider> {

    private Album<P>                         album;
    private ImmutableList<Media<P>>          files;
    private ImmutableList<MediaTimeFrame<P>> timeFrames;


    /**
     * Create a new {@link AlbumData} instance.
     * 
     * @param album
     *            The album whose data we hold.
     */
    public AlbumData(Album<P> album) {

        this.album = album;
    }

    /**
     * @return The album of this {@link AlbumData}.
     */
    public Album<P> getAlbum() {

        return album;
    }

    /**
     * @param files
     *            The files of this {@link AlbumData}.
     */
    public void setFiles(ImmutableList<Media<P>> files) {

        checkNotNull( files );

        this.files = files;
    }

    /**
     * @return The files of this {@link AlbumData}.
     */
    public ImmutableList<Media<P>> getFiles() {

        return files;
    }

    /**
     * @param timeFrames
     *            The timeFrames of this {@link AlbumData}.
     */
    public void setTimeFrames(ImmutableList<MediaTimeFrame<P>> timeFrames) {

        checkNotNull( timeFrames );

        this.timeFrames = timeFrames;
    }

    /**
     * @return The timeFrames of this {@link AlbumData}.
     */
    public ImmutableList<MediaTimeFrame<P>> getTimeFrames() {

        return timeFrames;
    }
}
