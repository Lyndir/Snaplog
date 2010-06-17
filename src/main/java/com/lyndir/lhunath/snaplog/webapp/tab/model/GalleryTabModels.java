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

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.model.EmptyModelProvider;
import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.object.media.AlbumProviderType;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.webapp.tab.GalleryTabPanel;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link GalleryTabModels}<br> <sub>Model provider for {@link GalleryTabPanel}.</sub></h2>
 *
 * <p> <i>Mar 11, 2010</i> </p>
 *
 * @author lhunath
 */
public class GalleryTabModels extends ModelProvider<GalleryTabModels, User> {

    static final Logger logger = Logger.get( GalleryTabModels.class );

    private final IModel<String> decoratedUsername;
    private final IModel<String> username;
    private final NewAlbumFormModels newAlbumForm;

    /**
     * @param model A model providing the user whose gallery to show.
     */
    @Inject
    public GalleryTabModels(final IModel<User> model) {

        super( model );

        decoratedUsername = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return getModelObject().toString();
            }
        };
        username = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return getModelObject().getUserName();
            }
        };

        newAlbumForm = new NewAlbumFormModels();
    }

    /**
     * <h2>{@link NewAlbumFormModels}<br> <sub>Model provider for the New Album form.</sub></h2>
     *
     * <p> <i>Mar 12, 2010</i> </p>
     *
     * @author lhunath
     */
    public class NewAlbumFormModels extends EmptyModelProvider<NewAlbumFormModels> {

        private final IModel<List<AlbumProviderType>> types;

        private final IModel<AlbumProviderType> type;
        private final IModel<String> name;
        private final IModel<String> description;

        NewAlbumFormModels() {

            types = new LoadableDetachableModel<List<AlbumProviderType>>() {

                @Override
                protected List<AlbumProviderType> load() {

                    return Arrays.asList( AlbumProviderType.values() );
                }
            };

            type = new Model<AlbumProviderType>();
            name = new Model<String>();
            description = new Model<String>();
        }

        // Accessors.

        /**
         * @return A model that holds the user-selected {@link AlbumProviderType} which will provide media for the new album.
         */
        public IModel<AlbumProviderType> type() {

            return type;
        }

        /**
         * @return A model that provides a list of available {@link AlbumProviderType}s.
         */
        public IModel<List<AlbumProviderType>> types() {

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
    public NewAlbumFormModels newAlbumForm() {

        return newAlbumForm;
    }
}
