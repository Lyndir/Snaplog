package com.lyndir.lhunath.snaplog.webapp.components;

import org.apache.wicket.markup.html.panel.Panel;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;


/**
 * <h2>{@link TagsView}<br>
 * <sub>Popup that allows users to manage and navigate media tags.</sub></h2>
 * 
 * <p>
 * <i>Jan 4, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class TagsView extends Panel {

    Messages     msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService albumService;


    /**
     * {@inheritDoc}
     * 
     * @param id
     *            The wicket ID of the tab.
     */
    public TagsView(String id) {

        super( id );
    }
}
