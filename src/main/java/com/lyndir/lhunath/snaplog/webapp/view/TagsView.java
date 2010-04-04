package com.lyndir.lhunath.snaplog.webapp.view;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import org.apache.wicket.model.IModel;


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
public class TagsView extends GenericPanel<Album> {

    @Inject
    AlbumService albumService;


    /**
     * @param id         The wicket ID of the tab.
     * @param albumModel The {@link Album} whose {@link Media} to scan for tags.
     */
    public TagsView(String id, IModel<Album> albumModel) {

        super( id, albumModel );
        checkNotNull( albumModel.getObject(), "Model object of TagsView must not be null" );
    }
}
