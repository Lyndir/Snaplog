package com.lyndir.lhunath.snaplog.webapp.view;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.Pair;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.provider.AbstractIteratorProvider;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.SecurityService;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import java.util.Iterator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link AccessView}<br> <sub>Popup that manages media access rights.</sub></h2>
 *
 * <p> <i>Jan 4, 2010</i> </p>
 *
 * @author lhunath
 */
public class AccessView extends GenericPanel<Album> {

    static final Logger logger = Logger.get( AccessView.class );
    static final Messages msgs = MessagesFactory.create( Messages.class );

    @Inject
    AlbumService albumService;

    @Inject
    UserService userService;

    @Inject
    SecurityService securityService;

    WebMarkupContainer content;

    /**
     * @param id         Wicket component ID.
     * @param albumModel The {@link Album} to configure access controls for.
     */
    public AccessView(final String id, final IModel<Album> albumModel) {

        super( id, albumModel );
        checkNotNull( albumModel.getObject(), "Model object of AccessView must not be null" );

        // TODO: Should abstract these popup windows.
        add( content = new WebMarkupContainer( "content" ) {
            {
                setOutputMarkupId( true );
                add( new DataView<Pair<User, Permission>>( "users", new AbstractIteratorProvider<Pair<User, Permission>>() {

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
                                        add( new Label( "label", permission.localizedString() ) );
                                    }

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {

                                        try {
                                            securityService.setUserPermission( SnaplogSession.get().newToken(),
                                                                               AccessView.this.getModelObject(), user, permission );

                                            target.addComponent( content );
                                        }
                                        catch (PermissionDeniedException e) {
                                            error( e.getLocalizedMessage() );
                                        }
                                    }
                                }.add( new CSSClassAttributeAppender( permission == userPermission? "on": null ) ) );
                            }
                        } );
                    }
                } );

                add( new Form<Object>( "otherUser" ) {

                    IModel<String> name = new Model<String>();

                    {
                        add( new RequiredTextField<String>( "name", name ) );
                        add( new ListView<Permission>( "permissions", ImmutableList.copyOf( Permission.values() ) ) {

                            @Override
                            protected void populateItem(final ListItem<Permission> permissionItem) {

                                final Permission permission = permissionItem.getModelObject();

                                permissionItem.add( new WebMarkupContainer( "toggle" ) {

                                    {
                                        add( permission.newSprite( "icon", 64 ) );
                                        add( new Label( "label", permission.localizedString() ) );
                                    }}.add( new AjaxFormSubmitBehavior( "onclick" ) {

                                    @Override
                                    protected void onSubmit(final AjaxRequestTarget target) {

                                        try {
                                            User user = userService.getUserWithUserName( name.getObject() );
                                            securityService.setUserPermission( SnaplogSession.get().newToken(),
                                                                               AccessView.this.getModelObject(), user, permission );

                                            target.addComponent( content );
                                        }
                                        catch (PermissionDeniedException e) {
                                            error( e.getLocalizedMessage() );
                                        }
                                        catch (UserNotFoundException e) {
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
            }} );
    }

    @Override
    public boolean isVisible() {

        return securityService.hasAccess( Permission.ADMINISTER, SnaplogSession.get().newToken(), getModelObject() );
    }

    interface Messages {

    }
}
