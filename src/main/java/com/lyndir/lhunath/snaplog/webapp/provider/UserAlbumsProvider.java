package com.lyndir.lhunath.snaplog.webapp.provider;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.db4o.ObjectSet;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.user.User;
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
public class UserAlbumsProvider implements IDataProvider<Album> {

    private UserService userService;

    private ObjectSet<Album> query;
    private IModel<User> ownerModel;


    /**
     * Create a new {@link UserAlbumsProvider} instance.
     * 
     * @param userService
     *            The Guice provided {@link UserService}.
     * @param ownerModel
     *            A model that provides the user who owns the {@link Album}s this provider will return.
     */
    public UserAlbumsProvider(UserService userService, IModel<User> ownerModel) {

        this.userService = userService;
        this.ownerModel = ownerModel;
    }

    private ObjectSet<Album> getQuery() {

        if (query == null)
            query = userService.queryAlbumsOfUser( SnaplogSession.get().newToken(), ownerModel.getObject() );

        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {

        query = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<? extends Album> iterator(int first, int count) {

        return getQuery().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {

        return getQuery().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IModel<Album> model(Album object) {

        return new Model<Album>( object );
    }
}
