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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.model.EmptyModelProvider;
import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.AlbumProviderType;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
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
public class GalleryTabModels extends ModelProvider<GalleryTabModels, GalleryTabPanel, User> {

    static final Logger logger = Logger.get( GalleryTabModels.class );

    private IModel<String> decoratedUsername;
    private IModel<String> username;
    private NewAlbumFormModels newAlbumForm;


    /**
     * <b>Do NOT forget to attach your component before using this model using {@link #attach(GalleryTabPanel)}</b>
     * 
     * @param model
     *            A model providing the user whose gallery to show.
     */
    @Inject
    public GalleryTabModels(IModel<User> model) {

        this( null, model );
    }

    /**
     * @param component
     *            The {@link GalleryTabPanel} we'll be attached to.
     * @param model
     *            A model providing the user whose gallery to show.
     */
    public GalleryTabModels(GalleryTabPanel component, IModel<User> model) {

        super( component, model );

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
     * <h2>{@link AlbumItemModels}<br>
     * <sub>[in short] (TODO).</sub></h2>
     * 
     * <p>
     * <i>Mar 15, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    public class AlbumItemModels extends ModelProvider<AlbumItemModels, Item<?>, Album> {

        private IModel<Media> cover;
        private IModel<String> title;
        private IModel<String> description;


        AlbumItemModels(Item<?> component, IModel<Album> model) {

            super( component, model );

            cover = new LoadableDetachableModel<Media>() {

                @Override
                protected Media load() {

                    AlbumService albumService = GuiceContext.get().getInstance( AlbumService.class );
                    Iterator<Media> it = albumService.iterateFiles( SnaplogSession.get().newToken(), getModelObject() );
                    if (it.hasNext())
                        return Iterators.getLast( it );

                    return null;
                }
            };
            title = new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    return getModelObject().getName();
                }
            };
            description = new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    return getModelObject().getDescription();
                }
            };
        }

        // Accessors.

        /**
         * @return A model that provides the media that should be used as the cover for the album.
         */
        public IModel<Media> cover() {

            return cover;
        }

        /**
         * @return A model that provides the string to use as a title for the album.
         */
        public IModel<String> title() {

            return title;
        }

        /**
         * @return A model that provides the string to use as a description for the album.
         */
        public IModel<String> description() {

            return description;
        }
    }


    /**
     * <h2>{@link NewAlbumFormModels}<br>
     * <sub>Model provider for the New Album form.</sub></h2>
     * 
     * <p>
     * <i>Mar 12, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    public class NewAlbumFormModels extends EmptyModelProvider<NewAlbumFormModels, Form<?>> {

        private IModel<List<? extends AlbumProviderType>> types;

        private IModel<AlbumProviderType> type;
        private IModel<String> name;
        private IModel<String> description;


        NewAlbumFormModels() {

            types = new LoadableDetachableModel<List<? extends AlbumProviderType>>() {

                @Override
                protected List<? extends AlbumProviderType> load() {

                    return Arrays.asList( AlbumProviderType.values() );
                }
            };

            type = new Model<AlbumProviderType>();
            name = new Model<String>();
            description = new Model<String>();
        }

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
     * @param item
     *            The item that this album will be rendered in.
     * @param albumModel
     *            The model that provides the album.
     * @return A model provider for album items.
     */
    public AlbumItemModels albumItem(Item<?> item, IModel<Album> albumModel) {

        return new AlbumItemModels( item, albumModel );
    }

    /**
     * @return An object that provides models for the newAlbum form.
     */
    public NewAlbumFormModels newAlbumForm() {

        return newAlbumForm;
    }
}
