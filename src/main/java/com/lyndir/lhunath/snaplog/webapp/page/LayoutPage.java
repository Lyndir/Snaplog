package com.lyndir.lhunath.snaplog.webapp.page;

import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import net.link.safeonline.wicket.component.linkid.LinkIDLoginLink;
import net.link.safeonline.wicket.component.linkid.LinkIDLogoutLink;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackagedTextTemplate;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.lib.wayward.component.AjaxLabelLink;
import com.lyndir.lhunath.lib.wayward.component.LabelLink;
import com.lyndir.lhunath.lib.wayward.component.RedirectToPageException;
import com.lyndir.lhunath.lib.wayward.model.ModelProvider;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import com.lyndir.lhunath.snaplog.webapp.tab.TabProvider;


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

    private static final Logger         logger        = Logger.get( LayoutPage.class );
    protected static final String       CONTENT_PANEL = "contentPanel";

    protected final LayoutPageModels<?> models        = getModelProvider();

    WebMarkupContainer                  userEntry;
    WebMarkupContainer                  userSummary;
    WebMarkupContainer                  tabsContainer;
    WebMarkupContainer                  container;


    /**
     * {@inheritDoc}
     */
    public LayoutPage() {

        logger.dbg( "Constructing %s", getClass() );

        // Page Title.
        Label pageTitle = new Label( "pageTitle", models.pageTitle() );

        // User Login.
        userEntry = new WebMarkupContainer( "userEntry" ) {

            @Override
            public boolean isVisible() {

                return SnaplogSession.get().getActiveUser() == null;
            }
        };
        userEntry.add( new Label( "userGuessWelcome", models.userGuessWelcome() ) );
        userEntry.add( new LinkIDLoginLink( "userLogin" ) );

        // User Summary.
        userSummary = new WebMarkupContainer( "userSummary" ) {

            @Override
            public boolean isVisible() {

                return SnaplogSession.get().getActiveUser() != null;
            }
        };
        userSummary.add( new Label( "userBadge", models.userBadge() ) );
        userSummary.add( new BookmarkablePageLink<Page>( "userName", Page.class ) {

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {

                replaceComponentTagBody( markupStream, openTag, "lhunath" );
            }
        } );
        userSummary.add( new LabelLink( "userMessages", models.userMessages() ) {

            @Override
            public boolean isVisible() {

                // TODO: return messageCount > 0;
                return true;
            }

            @Override
            public void onClick() {

            // TODO: do something.
            }
        } );
        userSummary.add( new LabelLink( "userRequests", models.userRequests() ) {

            @Override
            public boolean isVisible() {

                // TODO: return requestCount > 0;
                return true;
            }

            @Override
            public void onClick() {

            // TODO: do something.
            }
        } );
        userSummary.add( new LinkIDLogoutLink( "userLogout" ) );

        // Page Tab.
        tabsContainer = new WebMarkupContainer( "tabsContainer" );
        ListView<TabProvider> headTabs = new ListView<TabProvider>( "tabs", models.tabs() ) {

            @Override
            protected void populateItem(final ListItem<TabProvider> item) {

                item.add( new AjaxLabelLink( "link", models.tab( item.getModel() ).title() ) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        // TAB click.
                        SnaplogSession.get().setActiveTab( item.getModelObject() );
                        setContentPanel( getActiveTabPanel( CONTENT_PANEL ) );

                        target.addComponent( container );
                        target.addComponent( tabsContainer );
                    }
                } ).setVisible( item.getModelObject().getTab().isVisible() );
            }

            @Override
            protected ListItem<TabProvider> newItem(final int index) {

                ListItem<TabProvider> item = new ListItem<TabProvider>( index, getListItemModel( getModel(), index ) );
                item.add( CSSClassAttributeAppender.of( models.tab( item.getModel() ).styleClass() ) );

                return item;
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
     * @return The {@link ModelProvider} for this page.
     */
    protected LayoutPageModels<?> getModelProvider() {

        return new LayoutPageModels<Object>( null );
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
        TabProvider activeTab = SnaplogSession.get().getActiveTab();
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

    protected static String trackJS(Component trackComponent) {

        Map<String, Object> trackVariables = new HashMap<String, Object>();
        trackVariables.put( "pageView", trackComponent.getClass().getSimpleName() );

        JavaScriptTemplate trackJS = new JavaScriptTemplate(
                new PackagedTextTemplate( LayoutPage.class, "trackPage.js" ) );

        return trackJS.asString( trackVariables );
    }

}
