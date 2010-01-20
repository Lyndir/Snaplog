package com.lyndir.lhunath.snaplog.webapp.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.StringHeaderContributor;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.linkid.LinkIDLoginLink;
import com.lyndir.lhunath.snaplog.linkid.LinkIDLogoutLink;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.webapp.JavaScriptProvider;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.cookie.LastUserCookieManager;
import com.lyndir.lhunath.snaplog.webapp.tabs.AdministrationTab;
import com.lyndir.lhunath.snaplog.webapp.tabs.AlbumTab;
import com.lyndir.lhunath.snaplog.webapp.tabs.WorkbenchTab;


public class LayoutPage extends WebPage {

    private static final long serialVersionUID = 1L;
    static final Messages     msgs             = LocalizerFactory.getLocalizer( Messages.class );

    static List<ITab>         headTabsList;
    static {
        headTabsList = new ArrayList<ITab>( 2 );
        headTabsList.add( new AbstractTab( new Model<String>( msgs.albumTab() ) ) {

            @Override
            public Panel getPanel(String wicketId) {

                return new AlbumTab( wicketId );
            }
        } );
        headTabsList.add( new AbstractTab( new Model<String>( msgs.workbenchTab() ) ) {

            @Override
            public Panel getPanel(String wicketId) {

                return new WorkbenchTab( wicketId );
            }
        } );
        headTabsList.add( new AbstractTab( new Model<String>( msgs.administrationTab() ) ) {

            @Override
            public Panel getPanel(String wicketId) {

                return new AdministrationTab( wicketId );
            }
        } );
    }

    int                       selectedTabIndex;
    WebMarkupContainer        userEntry;
    WebMarkupContainer        userSummary;
    WebMarkupContainer        headTabsContainer;

    // TODO: Unhardcode.
    int                       messageCount     = 1;
    int                       requestCount     = 1;


    /**
     * @param pageTitle
     *            The contents of the <code>title</code> tag.
     * @param headTabsList
     *            A list of tabs to put in the header.
     */
    public LayoutPage() {

        if (headTabsList == null || headTabsList.isEmpty())
            throw new IllegalArgumentException( "headTabsList must not be null or empty." );

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

                User lastUser = LastUserCookieManager.getLastUser();

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

                replaceComponentTagBody( markupStream, openTag, messageCount == 1
                        ? msgs.userMessagesSingular( messageCount ): msgs.userMessagesPlural( messageCount ) );
            }

            @Override
            public boolean isVisible() {

                return messageCount > 0;
            }
        } );
        userSummary.add( new BookmarkablePageLink<Page>( "userRequests", Page.class ) {

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {

                replaceComponentTagBody( markupStream, openTag, requestCount == 1
                        ? msgs.userRequestsSingular( requestCount ): msgs.userRequestsPlural( requestCount ) );
            }

            @Override
            public boolean isVisible() {

                return requestCount > 0;
            }
        } );
        userSummary.add( new LinkIDLogoutLink( "userLogout" ) );

        // Page Tabs.
        headTabsContainer = new WebMarkupContainer( "headTabsContainer" );
        ListView<ITab> headTabs = new ListView<ITab>( "headTabs", headTabsList ) {

            @Override
            protected void populateItem(ListItem<ITab> item) {

                final ITab headTab = item.getModelObject();

                Link<String> link = new AjaxFallbackLink<String>( "link" ) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        // TAB click.
                        selectedTabIndex = headTabsList.indexOf( headTab );
                        Panel contentPanel = headTab.getPanel( "contentPanel" );
                        contentPanel.setOutputMarkupId( true );

                        // OnShowJavaScript
                        String panelJs = null;
                        if (contentPanel instanceof JavaScriptProvider)
                            panelJs = ((JavaScriptProvider) contentPanel).getProvidedJavaScript();
                        Map<String, Object> trackPanelVariables = new HashMap<String, Object>();
                        trackPanelVariables.put( "pageView", contentPanel.getClass().getSimpleName() );
                        final String trackPanelJs = new JavaScriptTemplate( new PackagedTextTemplate( LayoutPage.class,
                                "trackPage.js" ) ).asString( trackPanelVariables );

                        LayoutPage.this.addOrReplace( contentPanel );

                        if (target != null) {
                            // AJAX Support
                            target.addComponent( contentPanel );
                            target.addComponent( headTabsContainer );

                            if (panelJs != null)
                                target.appendJavascript( panelJs );
                            target.appendJavascript( "Shadowbox.setup();" );
                            target.appendJavascript( trackPanelJs );

                        } else {
                            // No AJAX Support
                            final String jsTemplate = panelJs;

                            add( new StringHeaderContributor( new JavaScriptTemplate( new TextTemplate() {

                                @Override
                                public TextTemplate interpolate(Map<String, Object> variables) {

                                    return this;
                                }

                                @Override
                                public String getString() {

                                    return jsTemplate + "\n\n" + trackPanelJs;
                                }
                            } ).asString() ) );
                        }
                    }
                };

                item.add( link );
                link.add( new Label( "title", headTab.getTitle() ) );
            }

            @Override
            protected ListItem<ITab> newItem(final int index) {

                return new ListItem<ITab>( index, getListItemModel( getModel(), index ) ) {

                    @Override
                    protected void onComponentTag(ComponentTag tag) {

                        super.onComponentTag( tag );
                        if (index == selectedTabIndex)
                            tag.put( "class", "active" );
                    }
                };
            }
        };
        headTabsContainer.add( headTabs );
        headTabsContainer.setOutputMarkupId( true );

        // Page Content.
        Panel contentPanel = getDefaultPanel( "contentPanel" );
        contentPanel.setOutputMarkupId( true );

        // Page Tracking.
        Map<String, Object> trackPageVariables = new HashMap<String, Object>();
        trackPageVariables.put( "pageView", contentPanel.getClass().getSimpleName() );
        add( new StringHeaderContributor( new JavaScriptTemplate( new PackagedTextTemplate( LayoutPage.class,
                "trackPage.js" ) ).asString( trackPageVariables ) ) );

        // OnShowJavaScript
        String js = null;
        if (contentPanel instanceof JavaScriptProvider)
            js = ((JavaScriptProvider) contentPanel).getProvidedJavaScript();
        final String jsTemplate = js;
        add( new StringHeaderContributor( new JavaScriptTemplate( new TextTemplate() {

            @Override
            public TextTemplate interpolate(Map<String, Object> variables) {

                return this;
            }

            @Override
            public String getString() {

                return jsTemplate;
            }
        } ).asString() ) );

        add( pageTitle, userEntry, userSummary, headTabsContainer, contentPanel );
    }

    /**
     * @param wicketId
     *            The wicket ID that the panel should have.
     * @return The {@link Panel} to show as the content before any tabs have been selected.
     */
    protected Panel getDefaultPanel(String wicketId) {

        return headTabsList.get( 0 ).getPanel( wicketId );
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
}
