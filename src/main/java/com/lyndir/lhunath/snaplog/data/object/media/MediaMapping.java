package com.lyndir.lhunath.snaplog.data.object.media;

import com.google.common.base.Objects;
import com.lyndir.lhunath.opal.security.ACL;
import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import org.jetbrains.annotations.NotNull;
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

    private final User owner;
    private final Media original;
    private final String mapping;
    private final Instant created = new Instant();

    private Duration validity;

    public MediaMapping(final User owner, final Media original, final Duration validity) {

        super( original.getName() );
        this.owner = owner;

        this.original = original;
        this.validity = validity;

        mapping = Integer.toHexString( hashCode() );

        getACL().setDefaultPermission( Permission.VIEW );
    }

    public Media getOriginal() {

        return original;
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

    @NotNull
    @Override
    public User getOwner() {

        return owner;
    }

    @Override
    public ACL getACL() {

        if (isExpired())
            // If this mapping expires, its ACL does not apply anymore.  Use the real media's ACL instead.
            return getOriginal().getACL();

        return super.getACL();
    }

    @Override
    public Source getParent() {

        return getOriginal().getSource();
    }

    @Override
    public String getName() {

        return getOriginal().getName();
    }

    @Override
    public Source getSource() {

        return null;
    }

    @Override
    public ReadableInstant shotTime() {

        return getOriginal().shotTime();
    }

    @Override
    public String getDateString() {

        return getOriginal().getDateString();
    }

    @Override
    public int compareTo(final Media o) {

        return getOriginal().compareTo( o );
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

        return Objects.hashCode( created, original );
    }

    @Override
    public String getLocalizedType() {

        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {

        return msgs.instance( getOriginal() );
    }

    @Override
    public String toString() {

        return String.format( "{mapping: %s, media=%s}", getMapping(), getOriginal() );
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
        String instance(Media media);
    }
}
