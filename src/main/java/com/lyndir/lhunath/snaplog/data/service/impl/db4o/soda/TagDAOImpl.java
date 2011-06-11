package com.lyndir.lhunath.snaplog.data.service.impl.db4o.soda;

import static com.google.common.base.Preconditions.*;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.logging.exception.AlreadyCheckedException;
import com.lyndir.lhunath.opal.system.util.DateUtils;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Tag;
import com.lyndir.lhunath.snaplog.data.object.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.data.service.TagDAO;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import java.util.List;


/**
 * <h2>{@link TagDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>10 26, 2010</i> </p>
 *
 * @author lhunath
 */
public class TagDAOImpl implements TagDAO {

    static final Logger logger = Logger.get( TagDAOImpl.class );

    private final ObjectContainer db;
    private final UserService userService;

    @Inject
    public TagDAOImpl(final ObjectContainer db, final UserService userService) {

        this.db = db;
        this.userService = userService;
    }

    @Override
    public void update(final Tag tag) {

        db.store( tag );
    }

    @Override
    public List<Tag> listTags(final Predicate<Tag> predicate) {

        checkNotNull( predicate, "Given predicate must not be null." );

        // TODO: Can't SODA this.  Maybe we should avoid this method altogether?
        return db.query( new com.db4o.query.Predicate<Tag>() {

            @Override
            public boolean match(final Tag candidate) {

                return predicate.apply( candidate );
            }
        } );
    }

    @Override
    public List<Media> listMedia(final Tag tag, final boolean ascending) {

        Query query = db.query();
        query.constrain( Media.class );

        // TODO: Set the sort order in nq. package too.
        if (ascending)
            query.descend( "name" ).orderAscending();
        else
            query.descend( "name" ).orderDescending();

        if (tag.isUntagged()) {
            query.descend( "owner" ) //
                    .constrain( tag.getOwner() );

            return ImmutableList.copyOf( Iterables.filter( query.<Media>execute(), new Predicate<Media>() {
                @Override
                public boolean apply(final Media input) {

                    return input.getTags().isEmpty();
                }
            } ) );
        } else {
            query.descend( "tags" ) //
                    .constrain( tag ).contains();

            return query.execute();
        }
    }

    @Override
    public Tag findTag(final User tagOwner, final String tagName) {

        DateUtils.startTiming( "findTag" );
        try {
            Query query = db.query();
            query.constrain( Tag.class ) //
                    .and( query.descend( "name" ) //
                                  .constrain( tagName ) ) //
                    .and( query.descend( "ownerProfile" ) //
                                  .constrain( userService.getProfile( SecurityToken.INTERNAL_USE_ONLY, tagOwner ) ) );

            ObjectSet<Tag> results = query.execute();
            if (results.hasNext()) {
                Tag result = results.next();
                checkState( !results.hasNext(), "Multiple tags found for tag owned by %s named %s", tagOwner, tagName );

                return result;
            }

            return null;
        }
        catch (PermissionDeniedException e) {
            throw new AlreadyCheckedException( e );
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }
}
