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

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.cookie.LastUserCookieManager;
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
 * @param <T>
 *            The type of the model for this page.
 * @author lhunath
 */
public class LayoutPageModels<T> extends ModelProvider<T> {

    protected final Messages                              msgs             = LocalizerFactory.getLocalizer( Messages.class );

    private IModel<String>                                pageTitle        = new LoadableDetachableModel<String>() {

                                                                               @Override
                                                                               protected String load() {

                                                                                   User user = SnaplogSession.get()
                                                                                                             .getFocussedUser();

                                                                                   if (user == null)
                                                                                       return msgs.pageTitle(
                                                                                                              ' ',
                                                                                                              msgs.userNameUnknown() );

                                                                                   return msgs.pageTitle(
                                                                                                          user.getBadge(),
                                                                                                          user.getUserName() );
                                                                               }
                                                                           };

    private IModel<String>                                userGuessWelcome = new LoadableDetachableModel<String>() {

                                                                               @Override
                                                                               protected String load() {

                                                                                   User lastUser = LastUserCookieManager.findLastUser();

                                                                                   if (lastUser == null)
                                                                                       return msgs.userWelcome(
                                                                                                                ' ',
                                                                                                                msgs.userNameUnknown() );

                                                                                   return msgs.userWelcomeBack(
                                                                                                                lastUser.getBadge(),
                                                                                                                lastUser.getUserName() );
                                                                               }
                                                                           };

    private IModel<String>                                userBadge        = new LoadableDetachableModel<String>() {

                                                                               @Override
                                                                               protected String load() {

                                                                                   User user = checkNotNull( SnaplogSession.get()
                                                                                                                           .getFocussedUser() );

                                                                                   return Character.toString( user.getBadge() );
                                                                               }
                                                                           };

    private IModel<String>                                userMessages     = new LoadableDetachableModel<String>() {

                                                                               @Override
                                                                               protected String load() {

                                                                                   // TODO: unhardcode.
                                                                                   int messageCount = 1;

                                                                                   if (messageCount == 1)
                                                                                       return msgs.userMessagesSingular( messageCount );

                                                                                   return msgs.userMessagesPlural( messageCount );
                                                                               }
                                                                           };

    private IModel<String>                                userRequests     = new LoadableDetachableModel<String>() {

                                                                               @Override
                                                                               protected String load() {

                                                                                   // TODO: unhardcode.
                                                                                   int requestCount = 1;

                                                                                   if (requestCount == 1)
                                                                                       return msgs.userRequestsSingular( requestCount );

                                                                                   return msgs.userRequestsPlural( requestCount );
                                                                               }
                                                                           };

    private IModel<? extends List<? extends TabProvider>> tabs             = new LoadableDetachableModel<List<? extends TabProvider>>() {

                                                                               @Override
                                                                               protected List<? extends TabProvider> load() {

                                                                                   return ImmutableList.of( Tab.values() );
                                                                               }
                                                                           };


    public class TabItem extends ModelProvider<TabProvider> {

        private IModel<String> styleClass = new LoadableDetachableModel<String>() {

                                              @Override
                                              protected String load() {

                                                  if (getModelObject() == SnaplogSession.get().getActiveTab())
                                                      return "active";

                                                  return "";
                                              }
                                          };


        // Accessors.

        /**
         * @param model
         *            The model of the tab component; which is the provider of the tab.
         */
        public TabItem(IModel<TabProvider> model) {

            super( model );
        }

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
     * @param model
     *            The model for this page.
     */
    public LayoutPageModels(IModel<T> model) {

        super( model );
    }

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
    public IModel<? extends List<? extends TabProvider>> tabs() {

        return tabs;
    }

    /**
     * @param model
     *            The model of the tab component; which is the provider of the tab.
     * @return The {@link ModelProvider} for the tab component.
     */
    public TabItem tab(IModel<TabProvider> model) {

        return new TabItem( model );
    }
}
