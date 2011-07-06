package com.lyndir.lhunath.snaplog.webapp.tool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.opal.security.Subject;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.opal.security.service.SecurityService;
import com.lyndir.lhunath.opal.system.collection.Pair;
import com.lyndir.lhunath.opal.system.error.IllegalRequestException;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.opal.wayward.provider.AbstractIteratorProvider;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.media.Tag;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.model.service.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.component.Sprites;
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
import org.apache.wicket.model.*;


/**
 * <h2>{@link AccessPopup}<br> <sub>Popup that manages media access rights.</sub></h2>
 *
 * <p> <i>Jan 4, 2010</i> </p>
 *
 * @author lhunath
 */
public class AccessPopup extends PopupPanel<Tag> {

    static final Logger logger = Logger.get( AccessPopup.class );
    static final Messages msgs = MessagesFactory.create( Messages.class );

    @Inject
    UserService userService;

    @Inject
    SecurityService securityService;

    /**
     * @param id         Wicket component ID.
     * @param tagModel The {@link Source} to configure access controls for.
     */
    public AccessPopup(final String id, final IModel<Tag> tagModel) {

        super( id, tagModel );
    }

    @Override
    protected void initContent(final WebMarkupContainer content) {

        // TODO: We have three times a list of Permissions, perhaps this should be extracted into a View?
        content.add( new DataView<Pair<Subject, Permission>>( "users", new AbstractIteratorProvider<Pair<Subject, Permission>>() {

            @Override
            protected Iterator<Pair<Subject, Permission>> load() {

                try {
                    return securityService.iterateSubjectPermissions( SnaplogSession.get().newToken(), getModelObject() );
                }
                catch (PermissionDeniedException e) {
                    error( e.getLocalizedMessage() );
                    return Iterators.emptyIterator();
                }
            }

            @Override
            public IModel<Pair<Subject, Permission>> model(final Pair<Subject, Permission> object) {

                return new Model<Pair<Subject, Permission>>( object );
            }

            @Override
            public int size() {

                try {
                    return securityService.countPermittedSubjects( SnaplogSession.get().newToken(), getModelObject() );
                }
                catch (PermissionDeniedException e) {
                    error( e.getLocalizedMessage() );
                    return 0;
                }
            }
        } ) {

            @Override
            protected void populateItem(final Item<Pair<Subject, Permission>> userItem) {

                final Subject subject = userItem.getModelObject().getKey();
                final Permission userPermission = userItem.getModelObject().getValue();

                userItem.add( new Label( "name", subject.getLocalizedInstance() ) );
                userItem.add( new ListView<Permission>( "permissions", ImmutableList.copyOf( Permission.values() ) ) {

                    @Override
                    protected void populateItem(final ListItem<Permission> permissionItem) {

                        final Permission permission = permissionItem.getModelObject();

                        permissionItem.add( new AjaxLink<Object>( "toggle" ) {

                            {
                                add( Sprites.of( permission, "icon", 64 ) );
                                add( new Label( "label", permission.getLocalizedInstance() ) );
                            }

                            @Override
                            public void onClick(final AjaxRequestTarget target) {

                                try {
                                    securityService.setPermission( SnaplogSession.get().newToken(), AccessPopup.this.getModelObject(),
                                                                   subject, permission );

                                    target.addComponent( content );
                                }
                                catch (PermissionDeniedException e) {
                                    error( e.getLocalizedMessage() );
                                }
                                catch (IllegalRequestException e) {
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
                                add( Sprites.of( permission, "icon", 64 ) );
                                add( new Label( "label", permission.getLocalizedInstance() ) );
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
                        }.add( new SimpleAttributeModifier( "title", permission.info( AccessPopup.this.getModelObject() ) ) )
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
            }
        } );

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
                                add( Sprites.of( permission, "icon", 64 ) );
                                add( new Label( "label", permission.getLocalizedInstance() ) );
                            }
                        }.add( new SimpleAttributeModifier( "title", permission.info( AccessPopup.this.getModelObject() ) ) )
                         .add( new AjaxFormSubmitBehavior( "onClick" ) {

                             @Override
                             protected void onSubmit(final AjaxRequestTarget target) {

                                 try {
                                     User user = userService.getUserWithUserName( name.getObject() );
                                     securityService.setPermission(
                                             SnaplogSession.get().newToken(), AccessPopup.this.getModelObject(), user, permission );

                                     target.addComponent( content );
                                 }
                                 catch (PermissionDeniedException e) {
                                     error( e.getLocalizedMessage() );
                                 }
                                 catch (UserNotFoundException e) {
                                     error( e.getLocalizedMessage() );
                                 }
                                 catch (IllegalRequestException e) {
                                     error( e.getLocalizedMessage() );
                                 }
                             }

                             @Override
                             protected void onError(final AjaxRequestTarget target) {

                             }
                         } ) );
                    }
                } );
            }
        } );
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

        private final IModel<Tag> model;

        /**
         * @param model The model that provides the tag whose access should be managed through this tool.
         */
        public Tool(final IModel<Tag> model) {

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
                                                            .hasAccess( Permission.ADMINISTER, SnaplogSession.get().newToken(),
                                                                        model.getObject() );
        }
    }
}
