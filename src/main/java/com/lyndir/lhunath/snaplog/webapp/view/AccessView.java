package com.lyndir.lhunath.snaplog.webapp.view;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;


/**
 * <h2>{@link AccessView}<br>
 * <sub>Popup that manages media access rights.</sub></h2>
 * 
 * <p>
 * <i>Jan 4, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that we can interface with.
 * @author lhunath
 */
public class AccessView extends GenericPanel<Album> {

    Messages     msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService albumService;


    /**
     * @param id
     *            Wicket component ID.
     * @param albumModel
     *            The {@link Album} to configure access controls for.
     */
    public AccessView(String id, IModel<Album> albumModel) {

        super( id, albumModel );

        add( new ListView<String>( "groups", new AbstractReadOnlyModel<List<String>>() {

            @Override
            public List<String> getObject() {

                return new LinkedList<String>();
            }
        } ) {

            @Override
            protected void populateItem(ListItem<String> groupItem) {

                String group = groupItem.getModelObject();

                groupItem.add( new Label( "name", group ) );

                // Hide the months in the year initially.
                groupItem.add( new ListView<String>( "permissions", Arrays.asList( "See", "Contribute" ) ) {

                    @Override
                    protected void populateItem(ListItem<String> permissionItem) {

                        String permission = permissionItem.getModelObject();

                        permissionItem.add( new CheckBox( "checkbox", new Model<Boolean>( false ) ) );
                        permissionItem.add( new Label( "label", permission ) );
                    }
                } );
            }
        } );
    }
}
