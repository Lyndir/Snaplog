package com.lyndir.lhunath.snaplog.webapp.page;

import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.link.safeonline.wicket.component.linkid.LinkIDLoginLink;
import net.link.safeonline.wicket.component.linkid.LinkIDLogoutLink;

import org.apache.wicket.Component;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackagedTextTemplate;

import com.lyndir.lhunath.lib.system.localization.LocalizerFactory;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.RedirectToPageException;
import com.lyndir.lhunath.snaplog.data.User;
import com.lyndir.lhunath.snaplog.messages.Messages;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.cookie.LastUserCookieManager;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;


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

    private static final Logger   logger        = Logger.get( LayoutPage.class );
    protected static final String CONTENT_PANEL = "contentPanel";

    final Messages                msgs          = LocalizerFactory.getLocalizer( Messages.class, this );

    WebMarkupContainer            userEntry;
    WebMarkupContainer            userSummary;
    WebMarkupContainer            tabsContainer;
    WebMarkupContainer            container;

    // TODO: Unhardcode.
    int                           messageCount  = 1;
    int                           requestCount  = 1;


    /**
     * {@inheritDoc}
     */
    public LayoutPage() {

        logger.dbg( "Constructing %s", getClass() );

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

        // Page Tab.
        tabsContainer = new WebMarkupContainer( "tabsContainer" );
        ListView<Tab> headTabs = new ListView<Tab>( "tabs", Arrays.asList( Tab.values() ) ) {

            @Override
            protected void populateItem(ListItem<Tab> item) {

                item.add( new AjaxLink<Tab>( "link", item.getModel() ) {

                    {
                        add( new Label( "title", getModelObject().getTab().getTitle() ) );
                    }


                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        // TAB click.
                        SnaplogSession.get().setActiveTab( getModelObject() );
                        setContentPanel( getActiveTabPanel( CONTENT_PANEL ) );

                        target.addComponent( container );
                        target.addComponent( tabsContainer );
                    }
                } );
            }

            @Override
            protected ListItem<Tab> newItem(final int index) {

                return new ListItem<Tab>( index, getListItemModel( getModel(), index ) ) {

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

                add( new StringHeaderContributor( trackJS( get( CONTENT_PANEL ) ) ) );

                super.onBeforeRender();
            }
        }).setOutputMarkupId( true ) );
        setContentPanel( getInitialContentPanel( CONTENT_PANEL ) );

        add( pageTitle, userEntry, userSummary, tabsContainer );
    }

    /**
     * Override me to define a custom panel to show initially when this page is constructed.
     * 
     * @param wicketId
     *            The wicket ID that the panel should use.
     * @return
     */
    protected Panel getInitialContentPanel(String wicketId) {

        return getActiveTabPanel( wicketId );
    }

    public Panel getActiveTabPanel(String wicketId) {

        // Find the active tab.
        Tab activeTab = SnaplogSession.get().getActiveTab();
        if (activeTab == null)
            SnaplogSession.get().setActiveTab( activeTab = Tab.DESKTOP );

        logger.dbg( "Showing active tab: %s", activeTab );
        if (!getPage().getClass().equals( LayoutPage.class ))
            throw new RedirectToPageException( LayoutPage.class );

        return activeTab.getTab().getPanel( wicketId );
    }

    public void setContentPanel(Panel contentPanel) {

        checkState( contentPanel.getId().equals( CONTENT_PANEL ) );

        logger.dbg( "Setting content panel to: %s", contentPanel.getClass() );

        container.addOrReplace( contentPanel );
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

    protected static String trackJS(Component trackComponent) {

        Map<String, Object> trackVariables = new HashMap<String, Object>();
        trackVariables.put( "pageView", trackComponent.getClass().getSimpleName() );

        JavaScriptTemplate trackJS = new JavaScriptTemplate(
                new PackagedTextTemplate( LayoutPage.class, "trackPage.js" ) );

        return trackJS.asString( trackVariables );
    }

}
