/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.webapp.tab.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.AlbumProviderType;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels;
import com.lyndir.lhunath.snaplog.webapp.tab.GalleryTabPanel;


/**
 * <h2>{@link GalleryTabModels}<br>
 * <sub>Model provider for {@link GalleryTabPanel}.</sub></h2>
 * 
 * <p>
 * <i>Mar 11, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class GalleryTabModels extends LayoutPageModels<User> {

    static final Logger    logger            = Logger.get( GalleryTabModels.class );

    private IModel<String> decoratedUsername = new LoadableDetachableModel<String>() {

                                                 @Override
                                                 protected String load() {

                                                     return getModelObject().toString();
                                                 }
                                             };
    private IModel<String> username          = new LoadableDetachableModel<String>() {

                                                 @Override
                                                 protected String load() {

                                                     return getModelObject().getUserName();
                                                 }
                                             };

    private NewAlbumForm   newAlbumForm      = new NewAlbumForm();


    public class NewAlbumForm implements Serializable {

        private IModel<AlbumProviderType>                 type        = new Model<AlbumProviderType>();
        private IModel<List<? extends AlbumProviderType>> types       = new LoadableDetachableModel<List<? extends AlbumProviderType>>() {

                                                                          @Override
                                                                          protected List<? extends AlbumProviderType> load() {

                                                                              return Arrays.asList( AlbumProviderType.values() );
                                                                          }
                                                                      };
        private IModel<String>                            name        = new Model<String>();
        private IModel<String>                            description = new Model<String>();


        // Accessors.

        /**
         * @return A model that holds the user-selected {@link AlbumProviderType} which will provide media for the new
         *         album.
         */
        public IModel<AlbumProviderType> type() {

            return type;
        }

        /**
         * @return A model that provides a list of available {@link AlbumProviderType}s.
         */
        public IModel<List<? extends AlbumProviderType>> types() {

            return types;
        }

        /**
         * @return A model that holds the user-specified name for the new album.
         */
        public IModel<String> name() {

            return name;
        }

        /**
         * @return A model that holds the user-specified description for the new album.
         */
        public IModel<String> description() {

            return description;
        }
    }


    // Accessors.

    /**
     * @param userModel
     *            A model providing the user whose gallery to show.
     */
    public GalleryTabModels(IModel<User> userModel) {

        super( userModel );
        logger.dbg( "created with model: %s", userModel );
    }

    /**
     * @return A model that provides a decorated version of the username of the gallery owner.
     */
    public IModel<String> decoratedUsername() {

        return decoratedUsername;
    }

    /**
     * @return A model that provides the username of the gallery owner.
     */
    public IModel<String> username() {

        return username;
    }

    /**
     * @return An object that provides models for the newAlbum form.
     */
    public NewAlbumForm newAlbumForm() {

        return newAlbumForm;
    }
}
