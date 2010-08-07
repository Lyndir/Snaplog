package com.lyndir.lhunath.snaplog.webapp.tool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.Pair;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.provider.AbstractIteratorProvider;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.IllegalOperationException;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.model.service.AlbumService;
import com.lyndir.lhunath.snaplog.model.service.SecurityService;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.util.Iterator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link AccessPopup}<br> <sub>Popup that manages media access rights.</sub></h2>
 *
 * <p> <i>Jan 4, 2010</i> </p>
 *
 * @author lhunath
 */
public class AccessPopup extends PopupPanel<Album> {

    static final Logger logger = Logger.get( AccessPopup.class );
    static final Messages msgs = MessagesFactory.create( Messages.class );

    @Inject
    AlbumService albumService;

    @Inject
    UserService userService;

    @Inject
    SecurityService securityService;

    /**
     * @param id         Wicket component ID.
     * @param albumModel The {@link Album} to configure access controls for.
     */
    public AccessPopup(final String id, final IModel<Album> albumModel) {

        super( id, albumModel );
    }

    @Override
    protected void initContent(final WebMarkupContainer content) {

        // TODO: We have three times a list of Permissions, perhaps this should be extracted into a View?
        content.add( new DataView<Pair<User, Permission>>( "users", new AbstractIteratorProvider<Pair<User, Permission>>() {

            @Override
            protected Iterator<Pair<User, Permission>> load() {

                try {
                    return securityService.iterateUserPermissions( SnaplogSession.get().newToken(), getModelObject() );
                }
                catch (PermissionDeniedException e) {
                    error( e.getLocalizedMessage() );
                    return Iterators.emptyIterator();
                }
            }

            @Override
            public IModel<Pair<User, Permission>> model(final Pair<User, Permission> object) {

                return new Model<Pair<User, Permission>>( object );
            }

            @Override
            public int size() {

                try {
                    return securityService.countPermittedUsers( SnaplogSession.get().newToken(), getModelObject() );
                }
                catch (PermissionDeniedException e) {
                    error( e.getLocalizedMessage() );
                    return 0;
                }
            }
        } ) {

            @Override
            protected void populateItem(final Item<Pair<User, Permission>> userItem) {

                final User user = userItem.getModelObject().getKey();
                final Permission userPermission = userItem.getModelObject().getValue();

                userItem.add( new Label( "name", user.toString() ) );
                userItem.add( new ListView<Permission>( "permissions", ImmutableList.copyOf( Permission.values() ) ) {

                    @Override
                    protected void populateItem(final ListItem<Permission> permissionItem) {

                        final Permission permission = permissionItem.getModelObject();

                        permissionItem.add( new AjaxLink<Object>( "toggle" ) {
                            {
                                add( permission.newSprite( "icon", 64 ) );
                                add( new Label( "label", permission.objectDescription() ) );
                            }

                            @Override
                            public void onClick(final AjaxRequestTarget target) {

                                try {
                                    securityService.setUserPermission( SnaplogSession.get().newToken(), AccessPopup.this.getModelObject(),
                                                                       user, permission );

                                    target.addComponent( content );
                                }
                                catch (PermissionDeniedException e) {
                                    error( e.getLocalizedMessage() );
                                }
                                catch (IllegalOperationException e) {
                                    error( e.getLocalizedMessage() );
                                }
                            }
                        }.add( new SimpleAttributeModifier( "title", permission.info( AccessPopup.this.getModelObject() ) ) ) //
                                .add( new CSSClassAttributeAppender( permission == userPermission? "on": null ) ) );
                    }
                } );
            }
        } );

        content.add( new WebMarkupContainer( "defaultUser" ) {

            {
                add( new ListView<Permission>( "permissions", ImmutableList.copyOf( Permission.values() ) ) {

                    @Override
                    protected void populateItem(final ListItem<Permission> permissionItem) {

                        final Permission permission = permissionItem.getModelObject();

                        permissionItem.add( new AjaxLink<Object>( "toggle" ) {

                            {
                                add( permission.newSprite( "icon", 64 ) );
                                add( new Label( "label", permission.objectDescription() ) );
                            }

                            @Override
                            public void onClick(final AjaxRequestTarget target) {

                                try {
                                    securityService.setDefaultPermission( SnaplogSession.get().newToken(),
                                                                          AccessPopup.this.getModelObject(), permission );

                                    target.addComponent( content );
                                }
                                catch (PermissionDeniedException e) {
                                    error( e.getLocalizedMessage() );
                                }
                            }
                        }.add( new SimpleAttributeModifier( "title", permission.info( AccessPopup.this.getModelObject() ) ) ) //
                                .add( CSSClassAttributeAppender.of( new LoadableDetachableModel<String>() {
                            @Override
                            protected String load() {

                                try {
                                    if (permission == securityService.getDefaultPermission( SnaplogSession.get().newToken(),
                                                                                            AccessPopup.this.getModelObject() ))
                                        return "on";
                                }
                                catch (PermissionDeniedException e) {
                                    error( e.getLocalizedMessage() );
                                }

                                return null;
                            }
                        } ) ) );
                    }
                } );
            }} );

        content.add( new Form<Object>( "otherUser" ) {

            final IModel<String> name = new Model<String>();

            {
                // Add the new user permission UI.
                add( new RequiredTextField<String>( "name", name ) );
                add( new ListView<Permission>( "permissions", ImmutableList.copyOf( Permission.values() ) ) {

                    @Override
                    protected void populateItem(final ListItem<Permission> permissionItem) {

                        final Permission permission = permissionItem.getModelObject();

                        permissionItem.add( new WebMarkupContainer( "toggle" ) {

                            {
                                add( permission.newSprite( "icon", 64 ) );
                                add( new Label( "label", permission.objectDescription() ) );
                            }}.add( new SimpleAttributeModifier( "title", permission.info( AccessPopup.this.getModelObject() ) ) ) //
                                .add( new AjaxFormSubmitBehavior( "onclick" ) {

                            @Override
                            protected void onSubmit(final AjaxRequestTarget target) {

                                try {
                                    User user = userService.getUserWithUserName( name.getObject() );
                                    securityService.setUserPermission( SnaplogSession.get().newToken(), AccessPopup.this.getModelObject(),
                                                                       user, permission );

                                    target.addComponent( content );
                                }
                                catch (PermissionDeniedException e) {
                                    error( e.getLocalizedMessage() );
                                }
                                catch (UserNotFoundException e) {
                                    error( e.getLocalizedMessage() );
                                }
                                catch (IllegalOperationException e) {
                                    error( e.getLocalizedMessage() );
                                }
                            }

                            @Override
                            protected void onError(final AjaxRequestTarget target) {

                            }
                        } ) );
                    }
                } );
            }} );
    }

    @Override
    public boolean isVisible() {

        return new Tool( getModel() ).isVisible();
    }

    interface Messages {

        /**
         * @return The title of the toolbar tool that will activate the Access popup.
         */
        IModel<String> tool();
    }


    public static class Tool implements SnaplogPanelTool {

        private final IModel<Album> model;

        /**
         * @param model The model that provides the album whose access should be managed through this tool.
         */
        public Tool(final IModel<Album> model) {

            this.model = model;
        }

        @Override
        public IModel<String> getTitle() {

            return msgs.tool();
        }

        @Override
        public IModel<String> getTitleClass() {

            return new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {

                    return "ss_sprite ss_key";
                }
            };
        }

        @Override
        public Panel getPanel(final String id) {

            return new AccessPopup( id, model );
        }

        @Override
        public boolean isVisible() {

            return model.getObject() != null && GuiceContext.getInstance( SecurityService.class )
                    .hasAccess( Permission.ADMINISTER, SnaplogSession.get().newToken(), model.getObject() );
        }
    }
}
