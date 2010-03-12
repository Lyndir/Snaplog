package com.lyndir.lhunath.snaplog.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;


/**
 * <h2>{@link AlbumData}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Jan 28, 2010</i>
 * </p>
 * 
 * @param The
 *            type of {@link Provider} that provides the resources cached by this album data.
 * @param <A>
 *            The type of {@link Album} we cache data for.
 * @param <M>
 *            The type of {@link Media} that is available from A.
 * @author lhunath
 */
public abstract class AlbumData {

    private List<Media>          files;
    private List<MediaTimeFrame> timeFrames;


    /**
     * @return The album of this {@link AlbumData}.
     */
    public abstract Album getAlbum();

    /**
     * @param files
     *            The files of this {@link AlbumData}.
     */
    public void setFiles(List<Media> files) {

        checkNotNull( files );

        this.files = files;
    }

    /**
     * @return The files of this {@link AlbumData}.
     */
    public List<Media> getFiles() {

        return files;
    }

    /**
     * @param timeFrames
     *            The timeFrames of this {@link AlbumData}.
     */
    public void setTimeFrames(List<MediaTimeFrame> timeFrames) {

        checkNotNull( timeFrames );

        this.timeFrames = timeFrames;
    }

    /**
     * @return The timeFrames of this {@link AlbumData}.
     */
    public List<MediaTimeFrame> getTimeFrames() {

        return timeFrames;
    }
}
