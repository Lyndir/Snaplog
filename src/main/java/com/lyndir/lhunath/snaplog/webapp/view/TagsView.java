package com.lyndir.lhunath.snaplog.webapp.view;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Provider;
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
 * @param <P>
 *            The type of {@link Provider} that we can interface with.
 * @author lhunath
 */
public class TagsView extends GenericPanel<Album<Provider>> {

    Messages               msgs = LocalizerFactory.getLocalizer( Messages.class, this );

    @Inject
    AlbumService<Provider> albumService;


    /**
     * @param id
     *            The wicket ID of the tab.
     * @param albumModel
     *            The {@link Album} whose {@link Media} to scan for tags.
     */
    public TagsView(String id, IModel<Album<Provider>> albumModel) {

        super( id, albumModel );
    }
}
