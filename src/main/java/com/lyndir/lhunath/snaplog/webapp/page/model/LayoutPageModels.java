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
package com.lyndir.lhunath.snaplog.webapp.page.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.model.EmptyModelProvider;
import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.cookie.LastUserCookieManager;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage.Messages;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import java.util.List;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link LayoutPageModels}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 11, 2010</i> </p>
 *
 * @author lhunath
 */
public class LayoutPageModels extends EmptyModelProvider<LayoutPageModels> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final IModel<String> pageTitle;
    private final IModel<String> userGuessWelcome;
    private final IModel<String> userBadge;
    private final IModel<String> userMessages;
    private final IModel<String> userRequests;
    private final IModel<? extends List<TabItem>> tabs;
    private final IModel<String> focusedUser;
    private final IModel<String> focusedContent;

    /**
     * Create a new {@link LayoutPageModels} instance.
     */
    public LayoutPageModels() {

        pageTitle = msgs.pageTitle();

        userGuessWelcome = msgs.userWelcome( new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                User lastUser = LastUserCookieManager.findLastUser();
                if (lastUser == null)
                    return null;

                return lastUser.getUserName();
            }
        } );

        userBadge = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                User user = checkNotNull( SnaplogSession.get().getFocusedUser(), "focused user must not be null." );

                return Character.toString( user.getBadge() );
            }
        };

        userMessages = msgs.userMessages( 1 );

        userRequests = msgs.userRequests( 1 );

        tabs = new LoadableDetachableModel<List<TabItem>>() {

            @Override
            protected List<TabItem> load() {

                return Lists.transform( ImmutableList.copyOf( Tab.values() ), new Function<Tab, TabItem>() {

                    @Override
                    public TabItem apply(final Tab from) {

                        return new TabItem( new Model<Tab>( from ) );
                    }
                } );
            }
        };

        focusedUser = msgs.focusedUser( new LoadableDetachableModel<User>() {

            @Override
            protected User load() {

                return SnaplogSession.get().getFocusedUser();
            }
        } );

        focusedContent = msgs.focusedContent( new LoadableDetachableModel<Album>() {

            @Override
            protected Album load() {

                return SnaplogSession.get().getFocusedAlbum();
            }
        } );
    }

    /**
     * <h2>{@link TabItem}<br> <sub>Model provider for {@link Tab} items.</sub></h2>
     *
     * <p> <i>Mar 12, 2010</i> </p>
     *
     * @author lhunath
     */
    public class TabItem extends ModelProvider<TabItem, Tab> {

        private final IModel<String> styleClass;

        /**
         * @param model The base model for the tab component.
         */
        public TabItem(final IModel<Tab> model) {

            super( model );

            styleClass = new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    if (getModelObject().get().equals( LayoutPage.getController().getActiveTab() ))
                        return "active";

                    return "";
                }
            };
        }

        // Accessors.

        /**
         * @return A model that provides the title for the current tab.
         */
        public IModel<String> title() {

            return getModelObject().get().getTitle();
        }

        /**
         * @return A model that provides the CSS <code>class</code> to apply to the tab's HTML element.
         */
        public IModel<String> styleClass() {

            return styleClass;
        }
    }

    // Accessors.

    /**
     * @return A model that provides the title of the current page.
     */
    public IModel<String> pageTitle() {

        return pageTitle;
    }

    /**
     * @return A model that provides a guess at the username of the current user.
     *
     * @see LastUserCookieManager#findLastUser()
     */
    public IModel<String> userGuessWelcome() {

        return userGuessWelcome;
    }

    /**
     * @return A model that provides the badge of the current user.
     *
     * @see SnaplogSession#getActiveUser()
     */
    public IModel<String> userBadge() {

        return userBadge;
    }

    /**
     * @return A model that provides the message count of the current user's unread messages.
     */
    public IModel<String> userMessages() {

        return userMessages;
    }

    /**
     * @return A model that provides the request count of the current user's unread requests.
     */
    public IModel<String> userRequests() {

        return userRequests;
    }

    /**
     * @return A model that provides a list of all the tabs on this page.
     */
    public IModel<? extends List<TabItem>> tabs() {

        return tabs;
    }

    /**
     * @return A model that provides a description of the focused user.
     *
     * @see SnaplogSession#getFocusedUser()
     */
    public IModel<String> focusedUser() {

        return focusedUser;
    }

    /**
     * @return A model that provides a description of the focused content.
     *
     * @see SnaplogSession#getFocusedAlbum()
     */
    public IModel<String> focusedContent() {

        return focusedContent;
    }
}
