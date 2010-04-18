package com.lyndir.lhunath.snaplog.webapp.view;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.KeyAppender;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.provider.AbstractListProvider;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.UserService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


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

    static final Messages msgs = MessagesFactory.create( Messages.class );

    @Inject
    AlbumService albumService;

    @Inject
    UserService userService;


    /**
     * @param id         Wicket component ID.
     * @param albumModel The {@link Album} to configure access controls for.
     */
    public AccessView(final String id, final IModel<Album> albumModel) {

        super( id, albumModel );
        checkNotNull( albumModel.getObject(), "Model object of AccessView must not be null" );

        add( new DataView<User>( "users", new AbstractListProvider<User>() {

            @Override
            protected List<User> loadObject() {

                return userService.queryUsers( null );
            }

            @Override
            public IModel<User> model(final User object) {

                return new Model<User>( object );
            }
        } ) {

            @Override
            protected void populateItem(final Item<User> userItem) {

                User user = userItem.getModelObject();

                userItem.add( new Label( "name", user.toString() ) );

                // Hide the months in the year initially.
                userItem.add( new ListView<Permission>( "permissions", Arrays.asList( Permission.values() ) ) {

                    @Override
                    protected void populateItem(final ListItem<Permission> permissionItem) {

                        Permission permission = permissionItem.getModelObject();

                        FormComponent<Boolean> checkbox = new CheckBox( "checkbox", new Model<Boolean>( false ) );
                        checkbox.setLabel( msgs.permission( permission ) );
                        permissionItem.add( checkbox, new SimpleFormComponentLabel( "label", checkbox ) );
                    }
                } );
            }
        } );
    }


    interface Messages {

        /**
         * @param permission The permission to explain.
         *
         * @return A model that provides the text which names the given permission.
         */
        IModel<String> permission(@KeyAppender Permission permission);
    }
}
