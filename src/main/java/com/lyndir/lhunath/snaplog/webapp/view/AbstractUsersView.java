package com.lyndir.lhunath.snaplog.webapp.view;

import java.util.List;

import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.lib.wayward.provider.AbstractListProvider;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;


/**
 * <h2>{@link AbstractUsersView}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 23, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class AbstractUsersView extends DataView<User> {

    @Inject
    AlbumService albumService;


    /**
     * Create a new {@link AbstractUsersView} instance.
     * 
     * @param id
     *            The wicket ID to bind this component on.
     * @param usersPerPage
     *            The maximum amount of users to show at once before hiding the rest behind a pager.
     */
    public AbstractUsersView(String id, int usersPerPage) {

        this( id, null, usersPerPage );
    }

    /**
     * Create a new {@link AbstractUsersView} instance.
     * 
     * @param id
     *            The wicket ID to bind this component on.
     * @param predicate
     *            An optional predicate that should evaluate to <code>true</code> for each user to return. If
     *            <code>null</code>, all users implicitly match.
     * @param usersPerPage
     *            The maximum amount of users to show at once before hiding the rest behind a pager.
     */
    public AbstractUsersView(String id, final IPredicate<User> predicate, int usersPerPage) {

        super( id, new AbstractListProvider<User>() {

            @Override
            public IModel<User> model(User object) {

                return new Model<User>( object );
            }

            @Override
            protected List<User> loadObject() {

                UserService userService = GuiceContext.get().getInstance( UserService.class );
                return userService.queryUsers( SnaplogSession.get().newToken(), predicate );
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
