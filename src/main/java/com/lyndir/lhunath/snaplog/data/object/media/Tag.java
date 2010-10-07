package com.lyndir.lhunath.snaplog.data.object.media;

import com.lyndir.lhunath.snaplog.data.object.security.AbstractSecureObject;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;
import net.link.safeonline.util.validation.annotation.NotNull;


/**
 * <h2>{@link Tag}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>10 03, 2010</i> </p>
 *
 * @author lhunath
 */
public class Tag extends AbstractSecureObject<UserProfile> {

    private final UserProfile ownerProfile;
    private final String name;
    private final String description;

    public Tag(@NotNull final UserProfile ownerProfile, final String name, final String description) {

        super( ownerProfile.getOwner() );

        this.ownerProfile = ownerProfile;
        this.name = name;
        this.description = description;
    }

    @Override
    public UserProfile getParent() {

        return ownerProfile;
    }

    @Override
    public String typeDescription() {

        // TODO: Fill in type description
        return null;
    }

    @Override
    public String objectDescription() {

        // TODO: Fill in instance description
        return null;
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }
}
