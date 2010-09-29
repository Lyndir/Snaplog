package com.lyndir.lhunath.snaplog.data.object.media;

import com.google.common.base.Objects;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.security.ACL;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import org.joda.time.*;


/**
 * <h2>{@link MediaMapping}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>09 21, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaMapping extends Media {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final Instant created = new Instant();
    private final Media realMedia;
    private final String mapping;

    private Duration validity;

    public MediaMapping(final User owner, final Media realMedia, final Duration validity) {

        super( realMedia.getName() );

        this.realMedia = realMedia;
        this.validity = validity;

        mapping = Integer.toHexString( hashCode() );

        getACL().setDefaultPermission( Permission.VIEW );
    }

    public Media getRealMedia() {

        return realMedia;
    }

    public Instant getCreated() {

        return created;
    }

    public Duration getValidity() {

        return validity;
    }

    public void setValidity(final Duration validity) {

        this.validity = validity;
    }

    public boolean isExpired() {

        return new Instant().isAfter( created.plus( validity ) );
    }

    @Override
    public ACL getACL() {

        if (isExpired())
            // If this mapping expires, its ACL does not apply anymore.  Use the real media's ACL instead.
            return getRealMedia().getACL();

        return super.getACL();
    }

    @Override
    public Album getParent() {

        return getRealMedia().getParent();
    }

    @Override
    public String getName() {

        return getRealMedia().getName();
    }

    @Override
    public Album getAlbum() {

        return null;
    }

    @Override
    public ReadableInstant shotTime() {

        return getRealMedia().shotTime();
    }

    @Override
    public String getDateString() {

        return getRealMedia().getDateString();
    }

    @Override
    public int compareTo(final Media o) {

        return getRealMedia().compareTo( o );
    }

    @Override
    public boolean equals(final Object o) {

        if (o == this)
            return true;
        if (!getClass().isInstance( o ))
            return false;

        return Objects.equal( ((MediaMapping) o).getMapping(), getMapping() );
    }

    @Override
    public int hashCode() {

        return Objects.hashCode( created, realMedia );
    }

    @Override
    public String typeDescription() {

        return msgs.type();
    }

    @Override
    public String objectDescription() {

        return msgs.description( getRealMedia() );
    }

    @Override
    public String toString() {

        return String.format( "{mapping: %s, media=%s}", getMapping(), getRealMedia() );
    }

    public String getMapping() {

        return mapping;
    }

    interface Messages {

        /**
         * @return The name of this type.
         */
        String type();

        /**
         * @param media The media that we provide access to.
         *
         * @return A description of this media mapping.
         */
        String description(Media media);
    }
}
