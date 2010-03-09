package com.lyndir.lhunath.snaplog.webapp.tab;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.db4o.ObjectSet;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;


/**
 * <h2>{@link UserAlbumsProvider}<br>
 * <sub>Provides {@link Album}s by a given user.</sub></h2>
 * 
 * <p>
 * Albums provided are owned by a given owner user and visible by the active user (or public).
 * </p>
 * 
 * <p>
 * <i>Mar 7, 2010</i>
 * </p>
 * 
 * @see SnaplogSession#getActiveUser()
 * @author lhunath
 */
final class UserAlbumsProvider<P extends Provider> implements IDataProvider<Album<P>> {

    private UserService<P>      userService;

    private ObjectSet<Album<P>> query;
    private IModel<User>        ownerModel;


    /**
     * Create a new {@link UserAlbumsProvider} instance.
     * 
     * @param userService
     *            The Guice provided {@link UserService}.
     * @param ownerModel
     *            A model that provides the user who owns the {@link Album}s this provider will return.
     */
    public UserAlbumsProvider(UserService<P> userService, IModel<User> ownerModel) {

        this.userService = userService;
        this.ownerModel = ownerModel;
    }

    private ObjectSet<Album<P>> getQuery() {

        if (query == null)
            query = userService.queryAlbumsOfUserVisibleToUser( ownerModel.getObject(), //
                                                                SnaplogSession.get().getActiveUser() );

        return query;
    }

    @Override
    public void detach() {

        query = null;
    }

    @Override
    public Iterator<? extends Album<P>> iterator(int first, int count) {

        return getQuery().iterator();
    }

    @Override
    public int size() {

        return getQuery().size();
    }

    @Override
    public IModel<Album<P>> model(Album<P> object) {

        return new Model<Album<P>>( object );
    }
}
