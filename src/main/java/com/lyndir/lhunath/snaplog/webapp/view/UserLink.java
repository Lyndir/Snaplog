package com.lyndir.lhunath.snaplog.webapp.view;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;

import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.util.LayoutPageUtils;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;

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
     * @param id
     *            The wicket ID to bind this component to.
     * @param model
     *            The model that provides the user to describe in the link and focus when clicked.
     */
    public UserLink(String id, IModel<User> model) {

        super( id, model );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(AjaxRequestTarget target) {

        SnaplogSession.get().setFocussedUser( getModelObject() );
        LayoutPageUtils.setActiveTab( Tab.GALLERY, target );
    }

    /**
     * {@inheritDoc}
     * 
     * @see Label#onComponentTagBody(org.apache.wicket.markup.MarkupStream, org.apache.wicket.markup.ComponentTag)
     */
    @Override
    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

        replaceComponentTagBody( markupStream, openTag, getDefaultModelObjectAsString() );
    }

    /**
     * {@inheritDoc}
     * 
     * @see Label#onComponentTag(org.apache.wicket.markup.ComponentTag)
     */
    @Override
    protected void onComponentTag(ComponentTag tag) {

        super.onComponentTag( tag );
        // always transform the tag to <span></span> so even labels defined as <span/> render
        tag.setType( XmlTag.OPEN );
    }
}