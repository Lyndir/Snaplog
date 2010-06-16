package com.lyndir.lhunath.snaplog.webapp.view;

import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link UserLink}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 24, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class UserLink extends AjaxLink<User> {

    /**
     * Create a new {@link UserLink} instance.
     *
     * @param id    The wicket ID to bind this component to.
     * @param model The model that provides the user to describe in the link and focus when clicked.
     */
    public UserLink(final String id, final IModel<User> model) {

        super( id, model );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final AjaxRequestTarget target) {

        SnaplogSession.get().setFocusedUser( getModelObject() );
        LayoutPage.setActiveTab( Tab.GALLERY, target );
    }

    /**
     * {@inheritDoc}
     *
     * @see Label#onComponentTagBody(MarkupStream, ComponentTag)
     */
    @Override
    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

        replaceComponentTagBody( markupStream, openTag, getDefaultModelObjectAsString() );
    }

    /**
     * {@inheritDoc}
     *
     * @see Label#onComponentTag(ComponentTag)
     */
    @Override
    protected void onComponentTag(final ComponentTag tag) {

        super.onComponentTag( tag );
        // always transform the tag to <span></span> so even labels defined as <span/> render
        tag.setType( XmlTag.OPEN );
    }
}
