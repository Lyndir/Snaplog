package com.lyndir.lhunath.snaplog.webapp.view;

import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.collection.SizedIterator;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.collection.IPredicate;
import com.lyndir.lhunath.opal.wayward.provider.AbstractSizedIteratorProvider;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.model.service.SourceService;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import org.apache.wicket.markup.repeater.data.DataView;


/**
 * <h2>{@link AbstractUsersView}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 23, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractUsersView extends DataView<User> {

    static final Logger logger = Logger.get( AbstractUsersView.class );

    @Inject
    SourceService sourceService;

    /**
     * Create a new {@link AbstractUsersView} instance.
     *
     * @param id           The wicket ID to bind this component on.
     * @param usersPerPage The maximum amount of users to show at once before hiding the rest behind a pager.
     */
    protected AbstractUsersView(final String id, final int usersPerPage) {

        this( id, null, usersPerPage );
    }

    /**
     * Create a new {@link AbstractUsersView} instance.
     *
     * @param id           The wicket ID to bind this component on.
     * @param predicate    An optional predicate that should evaluate to <code>true</code> for each user to return. If <code>null</code>,
     *                     all users implicitly match.
     * @param usersPerPage The maximum amount of users to show at once before hiding the rest behind a pager.
     */
    protected AbstractUsersView(final String id, final IPredicate<User> predicate, final int usersPerPage) {

        super( id, new AbstractSizedIteratorProvider<User>() {

            @Override
            protected SizedIterator<User> load() {

                return GuiceContext.getInstance( UserService.class ).iterateUsers( predicate );
            }
        }, usersPerPage );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return getItemCount() > 0;
    }
}
