package com.lyndir.lhunath.snaplog.webapp.view;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import com.db4o.ObjectSet;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;


/**
 * <h2>{@link AccessView}<br>
 * <sub>Popup that manages media access rights.</sub></h2>
 * 
 * <p>
 * <i>Jan 4, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class AccessView extends GenericPanel<Album> {

    Messages msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService albumService;

    @Inject
    UserService userService;


    /**
     * @param id
     *            Wicket component ID.
     * @param albumModel
     *            The {@link Album} to configure access controls for.
     */
    public AccessView(String id, IModel<Album> albumModel) {

        super( id, albumModel );

        add( new DataView<User>( "users", new IDataProvider<User>() {

            private ObjectSet<User> query = userService.queryUsers( SnaplogSession.get().newToken() );


            @Override
            public void detach() {

                query = null;
            }

            @Override
            public Iterator<? extends User> iterator(int first, int count) {

                return query.iterator();
            }

            @Override
            public int size() {

                return query.size();
            }

            @Override
            public IModel<User> model(User object) {

                return new Model<User>( object );
            }
        } ) {

            @Override
            protected void populateItem(Item<User> userItem) {

                User user = userItem.getModelObject();

                userItem.add( new Label( "name", user.toString() ) );

                // Hide the months in the year initially.
                userItem.add( new ListView<Permission>( "permissions", Arrays.asList( Permission.values() ) ) {

                    @Override
                    protected void populateItem(ListItem<Permission> permissionItem) {

                        Permission permission = permissionItem.getModelObject();

                        FormComponent<Boolean> checkbox = new CheckBox( "checkbox", new Model<Boolean>( false ) );
                        checkbox.setLabel( new StringResourceModel( permission.getLocalizationKey(), null ) );
                        permissionItem.add( checkbox, new SimpleFormComponentLabel( "label", checkbox ) );
                    }
                } );
            }
        } );
    }
}
