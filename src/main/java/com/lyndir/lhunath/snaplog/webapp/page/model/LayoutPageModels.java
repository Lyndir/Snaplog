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

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.wayward.model.EmptyModelProvider;
import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.cookie.LastUserCookieManager;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import com.lyndir.lhunath.snaplog.webapp.tab.TabProvider;


/**
 * <h2>{@link LayoutPageModels}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 11, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class LayoutPageModels extends EmptyModelProvider<LayoutPageModels, LayoutPage> {

    protected final Messages msgs = LocalizerFactory.getLocalizer( Messages.class );

    private IModel<String> pageTitle;
    private IModel<String> userGuessWelcome;
    private IModel<String> userBadge;
    private IModel<String> userMessages;
    private IModel<String> userRequests;
    private IModel<? extends List<TabItem>> tabs;


    /**
     * <b>Do NOT forget to attach your component before using this model using {@link #attach(LayoutPage)}</b>
     */
    public LayoutPageModels() {

        this( null );
    }

    /**
     * @param component
     *            The page we'll attach to.
     */
    public LayoutPageModels(LayoutPage component) {

        super( component );

        pageTitle = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                User user = SnaplogSession.get().getFocussedUser();

                if (user == null)
                    return msgs.pageTitle( ' ', msgs.userNameUnknown() );

                return msgs.pageTitle( user.getBadge(), user.getUserName() );
            }
        };

        userGuessWelcome = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                User lastUser = LastUserCookieManager.findLastUser();

                if (lastUser == null)
                    return msgs.userWelcome( ' ', msgs.userNameUnknown() );

                return msgs.userWelcomeBack( lastUser.getBadge(), lastUser.getUserName() );
            }
        };

        userBadge = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                User user = checkNotNull( SnaplogSession.get().getFocussedUser() );

                return Character.toString( user.getBadge() );
            }
        };

        userMessages = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                // TODO: unhardcode.
                int messageCount = 1;

                if (messageCount == 1)
                    return msgs.userMessagesSingular( messageCount );

                return msgs.userMessagesPlural( messageCount );
            }
        };

        userRequests = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                // TODO: unhardcode.
                int requestCount = 1;

                if (requestCount == 1)
                    return msgs.userRequestsSingular( requestCount );

                return msgs.userRequestsPlural( requestCount );
            }
        };

        tabs = new LoadableDetachableModel<List<TabItem>>() {

            @Override
            protected List<TabItem> load() {

                return Lists.transform( ImmutableList.of( Tab.values() ), new Function<TabProvider, TabItem>() {

                    @Override
                    public TabItem apply(final TabProvider from) {

                        return new TabItem( null, new Model<TabProvider>( from ) );
                    }
                } );
            }
        };
    }


    /**
     * <h2>{@link TabItem}<br>
     * <sub>Model provider for {@link Tab} items.</sub></h2>
     * 
     * <p>
     * <i>Mar 12, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    public static class TabItem extends ModelProvider<TabItem, ListItem<?>, TabProvider> {

        private IModel<String> styleClass;


        /**
         * @param component
         *            The tab component we'll attach to.
         * @param model
         *            The base model for the tab component.
         */
        public TabItem(ListItem<?> component, IModel<TabProvider> model) {

            super( component, model );

            styleClass = new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    if (getModelObject() == SnaplogSession.get().getActiveTab())
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

            return getModelObject().getTab().getTitle();
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
}
