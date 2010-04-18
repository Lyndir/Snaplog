package com.lyndir.lhunath.snaplog.data.media;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.SecurityService;
import com.lyndir.lhunath.snaplog.model.impl.SecurityServiceImpl;


/**
 * <h2>{@link AlbumData}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Jan 28, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public abstract class AlbumData {

    static final Logger logger = Logger.get( AlbumData.class );

    private List<Media> internalFiles;
    private List<MediaTimeFrame> internalTimeFrames;


    /**
     * @return The album of this {@link AlbumData}.
     */
    public abstract Album getAlbum();

    /**
     * @param internalFiles All files in the album.
     */
    public void setInternalFiles(final List<Media> internalFiles) {

        checkNotNull( internalFiles, "Given list of media internalFiles must not be null." );

        this.internalFiles = internalFiles;
        logger.dbg( "Setting %d internal files.", internalFiles.size() );
    }

    /**
     * <b>NOTE:</b> No authorization checks have been performed for these files. <b>Check authorization before returning
     * any information on these to the user.</b>
     *
     * @param onlyAllowedForSecurityService Only {@link SecurityService} is allowed to call this method. Therefore, this field must not be
     *                                      <code>null</code>.
     *
     * @return All files in the album.
     */
    public List<Media> getInternalFiles(final SecurityServiceImpl onlyAllowedForSecurityService) {

        checkNotNull( onlyAllowedForSecurityService, "Only securityService is allowed to use this method." );

        logger.dbg( "Returning %d internal files.", internalFiles.size() );
        return internalFiles;
    }

    /**
     * @return <code>true</code> only when the album's internalFiles are known.
     *
     * @see #setInternalFiles(List)
     */
    // TODO: Add cache timeout logic.
    public boolean hasInternalFiles() {

        return internalFiles != null;
    }

    /**
     * @param internalTimeFrames All the internalTimeFrames of the album.
     */
    public void setInternalTimeFrames(final List<MediaTimeFrame> internalTimeFrames) {

        checkNotNull( internalTimeFrames, "Given list of internalTimeFrames must not be null." );

        this.internalTimeFrames = internalTimeFrames;
    }

    /**
     * <b>NOTE:</b> No authorization checks have been performed for these files. <b>Check authorization before returning
     * any information on these to the user.</b>
     *
     * @param onlyAllowedForSecurityService Only {@link SecurityService} is allowed to call this method. Therefore, this field must not be
     *                                      <code>null</code>.
     *
     * @return All the internalTimeFrames in the album.
     */
    public List<MediaTimeFrame> getInternalTimeFrames(final SecurityServiceImpl onlyAllowedForSecurityService) {

        checkNotNull( onlyAllowedForSecurityService, "Only securityService is allowed to use this method." );

        return internalTimeFrames;
    }

    /**
     * @return <code>true</code> only when the album's internalTimeFrames are known.
     *
     * @see #setInternalTimeFrames(List)
     */
    // TODO: Add cache timeout logic.
    public boolean hasInternalTimeFrames() {

        return internalTimeFrames != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "{albumData: internalFiles=%d}", internalFiles == null? -1: internalFiles.size() );
    }
}
