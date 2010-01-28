package com.lyndir.lhunath.snaplog.webapp.components;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import org.apache.wicket.markup.html.panel.Panel;


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

    Messages msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService albumService;


    /**
     * {@inheritDoc}
     */
    public TagsView(String id) {

        super( id );
    }
}
