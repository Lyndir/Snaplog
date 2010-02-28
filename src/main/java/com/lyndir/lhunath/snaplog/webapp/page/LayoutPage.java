package com.lyndir.lhunath.snaplog.webapp.page;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.link.safeonline.wicket.component.linkid.LinkIDLoginLink;
import net.link.safeonline.wicket.component.linkid.LinkIDLogoutLink;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.StringHeaderContributor;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackagedTextTemplate;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.cookie.LastUserCookieManager;
import com.lyndir.lhunath.snaplog.webapp.tabs.Tabs;


/**
 * <h2>{@link LayoutPage}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Jan 28, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class LayoutPage extends WebPage {

    private static final long serialVersionUID = 1L;

    final Messages            msgs             = LocalizerFactory.getLocalizer( Messages.class, this );

    WebMarkupContainer        userEntry;
    WebMarkupContainer        userSummary;
    WebMarkupContainer        tabsContainer;
    WebMarkupContainer        container;

    // TODO: Unhardcode.
    int                       messageCount     = 1;
    int                       requestCount     = 1;


    /**
     * {@inheritDoc}
     */
    public LayoutPage() {

        // Page Title.
        Label pageTitle = new Label( "pageTitle", getPageTitle() );

        // User Login.
        userEntry = new WebMarkupContainer( "userEntry" ) {

            @Override
            public boolean isVisible() {

                return SnaplogSession.get().getActiveUser() == null;
            }
        };
        userEntry.add( new Label( "userGuessWelcome", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                User lastUser = LastUserCookieManager.findLastUser();

                if (lastUser == null)
                    return msgs.userWelcome( ' ', msgs.userNameUnknown() );

                return msgs.userWelcomeBack( lastUser.getBadge(), lastUser.getUserName() );
            }
        } ) );
        userEntry.add( new LinkIDLoginLink( "userLogin" ) );

        // User Summary.
        userSummary = new WebMarkupContainer( "userSummary" ) {

            @Override
            public boolean isVisible() {

                return SnaplogSession.get().getActiveUser() != null;
            }
        };
        userSummary.add( new Label( "userBadge", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                return "~";
            }
        } ) );
        userSummary.add( new BookmarkablePageLink<Page>( "userName", Page.class ) {

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {

                replaceComponentTagBody( markupStream, openTag, "lhunath" );
            }
        } );
        userSummary.add( new BookmarkablePageLink<Page>( "userMessages", Page.class ) {

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {

                if (messageCount == 1)
                    replaceComponentTagBody( markupStream, openTag, msgs.userMessagesSingular( messageCount ) );
                else
                    replaceComponentTagBody( markupStream, openTag, msgs.userMessagesPlural( messageCount ) );
            }

            @Override
            public boolean isVisible() {

                return messageCount > 0;
            }
        } );
        userSummary.add( new BookmarkablePageLink<Page>( "userRequests", Page.class ) {

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {

                if (requestCount == 1)
                    replaceComponentTagBody( markupStream, openTag, msgs.userRequestsSingular( requestCount ) );
                else
                    replaceComponentTagBody( markupStream, openTag, msgs.userRequestsPlural( requestCount ) );
            }

            @Override
            public boolean isVisible() {

                return requestCount > 0;
            }
        } );
        userSummary.add( new LinkIDLogoutLink( "userLogout" ) );

        // Page Tabs.
        tabsContainer = new WebMarkupContainer( "tabsContainer" );
        ListView<Tabs> headTabs = new ListView<Tabs>( "tabs", Arrays.asList( Tabs.values() ) ) {

            @Override
            protected void populateItem(ListItem<Tabs> item) {

                item.add( new AjaxLink<Tabs>( "link", item.getModel() ) {

                    {
                        add( new Label( "title", getModelObject().getTab().getTitle() ) );
                    }


                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        // TAB click.
                        SnaplogSession.get().setActiveTab( getModelObject() );

                        final String trackPanelJs = newActiveTabTrackJS();

                        target.addComponent( container );
                        target.addComponent( tabsContainer );
                        target.appendJavascript( trackPanelJs );
                    }
                } );
            }

            @Override
            protected ListItem<Tabs> newItem(final int index) {

                return new ListItem<Tabs>( index, getListItemModel( getModel(), index ) ) {

                    @Override
                    protected void onComponentTag(ComponentTag tag) {

                        super.onComponentTag( tag );

                        if (getModelObject() == SnaplogSession.get().getActiveTab())
                            tag.put( "class", "active" );
                    }
                };
            }
        };
        tabsContainer.add( headTabs );
        tabsContainer.setOutputMarkupId( true );

        // Page Content.
        add( (container = new WebMarkupContainer( "container" ) {

            @Override
            protected void onBeforeRender() {

                // Find the active tab.
                Tabs activeTab = SnaplogSession.get().getActiveTab();
                if (activeTab == null)
                    SnaplogSession.get().setActiveTab( activeTab = Tabs.DESKTOP );

                // Add/replace it in the layout.
                addOrReplace( activeTab.getTab().getPanel( "contentPanel" ) );
                add( new StringHeaderContributor( newActiveTabTrackJS() ) );

                super.onBeforeRender();
            }
        }).setOutputMarkupId( true ) );

        // Page Tracking.
        add( pageTitle, userEntry, userSummary, tabsContainer );
    }

    /**
     * @return The title string that describes this page.
     */
    protected String getPageTitle() {

        User user = SnaplogSession.get().getActiveUser();

        if (user == null)
            return msgs.pageTitle( ' ', msgs.userNameUnknown() );

        return msgs.pageTitle( user.getBadge(), user.getUserName() );
    }

    String newActiveTabTrackJS() {

        Map<String, Object> trackPanelVariables = new HashMap<String, Object>();
        trackPanelVariables.put( "pageView", SnaplogSession.get().getActiveTab().getTab().getClass().getSimpleName() );
        final String trackPanelJs = new JavaScriptTemplate( new PackagedTextTemplate( LayoutPage.class, "trackPage.js" ) ).asString( trackPanelVariables );
        return trackPanelJs;
    }

}
