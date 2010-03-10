package com.lyndir.lhunath.snaplog.webapp.view;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.FixedDeque;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.snaplog.data.Album;
import com.lyndir.lhunath.snaplog.data.Media;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.data.Media.Quality;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.servlet.ImageServlet;


/**
 * <h2>{@link BrowserView}<br>
 * <sub>Component that allows users to browse through media chronologically.</sub></h2>
 * 
 * <p>
 * <i>Jan 6, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that we can interface with.
 * @author lhunath
 */
public class BrowserView extends GenericPanel<Album<Provider>> {

    static final Logger    logger              = Logger.get( BrowserView.class );

    static final int       BROWSER_SIDE_IMAGES = 4;

    @Inject
    AlbumService<Provider> albumService;

    Media<?>               currentFile;
    IModel<Date>           currentTimeModel;


    /**
     * Create a new {@link BrowserView} instance.
     * 
     * @param id
     *            The wicket ID to put this component in the HTML.
     * @param albumModel
     *            The model contains the {@link Album} that the browser should get its media from.
     * @param currentTimeModel
     *            The model contains the {@link Date} upon which the browser should focus. The first image on or past
     *            this date will be the focussed image.
     */
    public BrowserView(String id, IModel<Album<Provider>> albumModel, IModel<Date> currentTimeModel) {

        super( id, albumModel );
        setOutputMarkupId( true );

        this.currentTimeModel = currentTimeModel;

        add( new BrowserListView( "photos" ) );
    }


    /**
     * <h2>{@link BrowserListView}<br>
     * <sub>A {@link ListView} which enumerates {@link Media}s.</sub></h2>
     * 
     * <p>
     * <i>Jan 6, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    private final class BrowserListView extends ListView<Media<Provider>> {

        BrowserListView(String id) {

            super( id, new BrowserFilesModel() );
        }

        @Override
        protected void populateItem(ListItem<Media<Provider>> item) {

            Media<Provider> media = item.getModelObject();
            Quality imageQuality = media.equals( currentFile )? Quality.PREVIEW: Quality.THUMBNAIL;
            final long shotTime = media.shotTime();
            WebMarkupContainer link = null;
            switch (imageQuality) {
                case ORIGINAL:
                case FULLSCREEN:
                case PREVIEW:
                    link = new WebMarkupContainer( "link" );
                break;

                case METADATA:
                case THUMBNAIL:
                    link = new AjaxFallbackLink<String>( "link" ) {

                        @Override
                        public void onClick(AjaxRequestTarget target) {

                            currentTimeModel.setObject( new Date( shotTime ) );
                            target.addComponent( BrowserView.this );
                        }
                    };
                break;
            }
            if (link == null) // Silence Eclipse's "Potential Null Pointer Access"
                throw logger.bug( "Uninitialized link" ).toError();

            link.add( new Label( "caption", media.getDateString() ) );
            link.add( new ContextImage( "photo", ImageServlet.getContextRelativePathFor( media, imageQuality ) ) );
            item.add( new ContextImage( "fullscreenPhoto", //
                    ImageServlet.getContextRelativePathFor( media, Quality.FULLSCREEN ) ).setVisible( imageQuality == Quality.PREVIEW ) );
            item.add( new AttributeAppender( "class", new Model<String>( imageQuality.getName() ), " " ) );
            item.add( link );
        }
    }


    /**
     * <h2>{@link BrowserFilesModel}<br>
     * <sub>A {@link Model} that enumerates all files the browser should display when centered on a certain point in
     * time.</sub></h2>
     * 
     * <p>
     * <i>Jan 6, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    private final class BrowserFilesModel extends AbstractReadOnlyModel<List<Media<Provider>>> {

        /**
         * Create a new {@link BrowserFilesModel} instance.
         */
        BrowserFilesModel() {

        }

        @Override
        public List<Media<Provider>> getObject() {

            List<? extends Media<Provider>> allFiles = albumService.getFiles( getModelObject() );
            FixedDeque<Media<Provider>> files = new FixedDeque<Media<Provider>>( BROWSER_SIDE_IMAGES * 2 + 1 );

            Iterator<? extends Media<Provider>> it = allFiles.iterator();
            if (!it.hasNext())
                return new LinkedList<Media<Provider>>();

            // Find the current file.
            boolean addedNextFile = false;
            while (it.hasNext()) {
                Media<Provider> nextFile = it.next();
                files.addFirst( nextFile );

                if (currentTimeModel.getObject() != null
                    && nextFile.shotTime() > currentTimeModel.getObject().getTime()) {
                    addedNextFile = true;
                    break;
                }

                currentFile = nextFile;
            }

            // Add the side images past the current file.
            // We already have the ones before it AND one of these if addedNextFile is set.
            for (int i = 0; i < BROWSER_SIDE_IMAGES - (addedNextFile? 1: 0); ++i)
                if (it.hasNext())
                    files.addFirst( it.next() );
                else
                    files.removeLast();

            return new LinkedList<Media<Provider>>( files );
        }
    }
}
