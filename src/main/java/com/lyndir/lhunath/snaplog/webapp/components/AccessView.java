package com.lyndir.lhunath.snaplog.webapp.components;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
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
 * @author lhunath
 */
public class AccessView extends Panel {

    Messages     msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService albumService;


    public AccessView(String id) {

        super( id );

        add( new ListView<String>( "groups", new AbstractReadOnlyModel<List<String>>() {

            @Override
            public List<String> getObject() {

                return new LinkedList<String>();
            }
        } ) {

            @Override
            protected void populateItem(final ListItem<String> groupItem) {

                final String group = groupItem.getModelObject();

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
