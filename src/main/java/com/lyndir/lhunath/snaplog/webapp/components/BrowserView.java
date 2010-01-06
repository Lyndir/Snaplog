package com.lyndir.lhunath.snaplog.webapp.components;

import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.model.MediaFile;
import com.lyndir.lhunath.snaplog.model.MediaService;
import com.lyndir.lhunath.snaplog.model.MediaFile.Quality;


/**
 * <h2>{@link BrowserView}<br>
 * <sub>Component that allows users to browse through media chronologically.</sub></h2>
 * 
 * <p>
 * <i>Jan 6, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public final class BrowserView extends Panel {

    static final Logger        logger              = Logger.get( BrowserView.class );

    protected static final int BROWSER_SIDE_IMAGES = 4;

    protected MediaFile        currentFile;


    /**
     * Create a new {@link BrowserView} instance.
     * 
     * @param id
     *            The wicket ID to put this component in the HTML.
     * @param currentTimeModel
     *            The model contains the {@link Date} upon which the browser should focus. The first image on or past
     *            this date will be the focussed image.
     */
    public BrowserView(String id, final IModel<Date> currentTimeModel) {

        super( id, currentTimeModel );
        setOutputMarkupId( true );

        add( new BrowserListView( "photos", currentTimeModel ) );
    }


    /**
     * <h2>{@link BrowserListView}<br>
     * <sub>A {@link ListView} which enumerates {@link MediaFile}s.</sub></h2>
     * 
     * <p>
     * <i>Jan 6, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    private final class BrowserListView extends ListView<MediaFile> {

        final IModel<Date> currentTimeModel;


        /**
         * Create a new {@link BrowserListView} instance.
         */
        BrowserListView(String id, IModel<Date> currentTimeModel) {

            super( id, new BrowserFilesModel( currentTimeModel ) );

            this.currentTimeModel = currentTimeModel;
        }

        @Override
        protected void populateItem(ListItem<MediaFile> item) {

            final MediaFile file = item.getModelObject();

            Quality imageQuality = Quality.THUMBNAIL;
            if (file.equals( currentFile ))
                imageQuality = Quality.SIZED;
            item.add( new AttributeAppender( "class", new Model<String>( imageQuality.getName() ), " " ) );

            AjaxFallbackLink<String> link = new AjaxFallbackLink<String>( "link" ) {

                @Override
                public void onClick(AjaxRequestTarget target) {

                    currentTimeModel.setObject( new Date( file.shotTime() ) );
                    target.addComponent( BrowserView.this );
                }
            };
            item.add( link );

            link.add( new Label( "caption", file.getDateString() ) );
            link.add( new Image( "photo", file.newResourceReference( imageQuality ) ) );
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
    private final class BrowserFilesModel extends AbstractReadOnlyModel<List<MediaFile>> {

        private final IModel<Date> currentTimeModel;


        /**
         * Create a new {@link BrowserFilesModel} instance.
         */
        BrowserFilesModel(IModel<Date> currentTimeModel) {

            this.currentTimeModel = currentTimeModel;
        }

        @Override
        public List<MediaFile> getObject() {

            Deque<MediaFile> allFiles = MediaService.getAllFiles();
            LinkedHashMap<MediaFile, ?> files = new LinkedHashMap<MediaFile, Object>( BROWSER_SIDE_IMAGES * 2 + 1 ) {

                @Override
                protected boolean removeEldestEntry(Map.Entry<MediaFile, Object> eldest) {

                    return size() > BROWSER_SIDE_IMAGES * 2 + 1;
                }
            };

            Iterator<MediaFile> it = allFiles.descendingIterator();
            if (!it.hasNext())
                return null;

            // Find the current file.
            while (it.hasNext()) {
                MediaFile nextFile = it.next();

                if (nextFile.shotTime() < currentTimeModel.getObject().getTime())
                    break;

                currentFile = nextFile;
                files.put( currentFile, null );
            }

            // Add the side images past the current file (we already have the ones before it).
            for (int i = 0; i < BROWSER_SIDE_IMAGES; ++i)
                files.put( it.next(), null );

            return ImmutableList.copyOf( files.keySet() );
        }
    }
}
