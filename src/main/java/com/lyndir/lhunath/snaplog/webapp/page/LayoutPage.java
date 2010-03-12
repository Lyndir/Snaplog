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
import com.lyndir.lhunath.lib.wayward.component.GenericWebPage;
import com.lyndir.lhunath.lib.wayward.component.LabelLink;
import com.lyndir.lhunath.lib.wayward.component.RedirectToPageException;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels.TabItem;
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
public class LayoutPage extends GenericWebPage<LayoutPageModels> {

    private static final Logger logger = Logger.get( LayoutPage.class );
    protected static final String CONTENT_PANEL = "contentPanel";

    WebMarkupContainer userEntry;
    WebMarkupContainer userSummary;
    WebMarkupContainer tabsContainer;
    WebMarkupContainer container;


    /**
     * Create a new {@link LayoutPage}.
     */
    public LayoutPage() {

        super( new LayoutPageModels().getModel() );
        getModelObject().attach( this );

        // Page Title.
        Label pageTitle = new Label( "pageTitle", getModelObject().pageTitle() );

        // User Login.
        userEntry = new WebMarkupContainer( "userEntry" ) {

            @Override
            public boolean isVisible() {

                return SnaplogSession.get().getActiveUser() == null;
            }
        };
        userEntry.add( new Label( "userGuessWelcome", getModelObject().userGuessWelcome() ) );
        userEntry.add( new LinkIDLoginLink( "userLogin" ) );

        // User Summary.
        userSummary = new WebMarkupContainer( "userSummary" ) {

            @Override
            public boolean isVisible() {

                return SnaplogSession.get().getActiveUser() != null;
            }
        };
        userSummary.add( new Label( "userBadge", getModelObject().userBadge() ) );
        userSummary.add( new BookmarkablePageLink<Page>( "userName", Page.class ) {

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {

                replaceComponentTagBody( markupStream, openTag, "lhunath" );
            }
        } );
        userSummary.add( new LabelLink( "userMessages", getModelObject().userMessages() ) {

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
        userSummary.add( new LabelLink( "userRequests", getModelObject().userRequests() ) {

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
        ListView<TabItem> headTabs = new ListView<TabItem>( "tabs", getModelObject().tabs() ) {

            @Override
            protected void populateItem(ListItem<TabItem> item) {

                final TabItem itemModel = item.getModelObject();
                itemModel.attach( item );

                item.add( new AjaxLabelLink( "link", itemModel.title() ) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        if (!getPage().getClass().equals( LayoutPage.class ))
                            throw new RedirectToPageException( LayoutPage.class );

                        // TAB click.
                        SnaplogSession.get().setActiveTab( itemModel.getObject() );
                        setContentPanel( getActiveTabPanel( CONTENT_PANEL ) );

                        target.addComponent( container );
                        target.addComponent( tabsContainer );
                    }
                } );
                item.add( CSSClassAttributeAppender.of( item.getModelObject().styleClass() ) );
                item.setVisible( itemModel.getObject().getTab().isVisible() );
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
     * @return The panel to show when the page first loads.
     */
    protected Panel getInitialContentPanel(String wicketId) {

        return getActiveTabPanel( wicketId );
    }

    /**
     * Load the given panel as the page content.
     * 
     * <p>
     * <b>NOTE:</b> The panel MUST use {@value #CONTENT_PANEL} as its wicket ID.
     * </p>
     * 
     * @param contentPanel
     *            The panel to show as the page content.
     */
    protected void setContentPanel(Panel contentPanel) {

        checkState( contentPanel.getId().equals( CONTENT_PANEL ) );

        logger.dbg( "Setting content panel to: %s", contentPanel.getClass() );

        container.addOrReplace( contentPanel );
    }

    /**
     * @param wicketId
     *            The wicket ID to create the panel with.
     * @return The panel for the active tab.
     * 
     * @see SnaplogSession#getActiveTab()
     */
    protected static Panel getActiveTabPanel(String wicketId) {

        // Find the active tab.
        TabProvider activeTab = SnaplogSession.get().getActiveTab();
        if (activeTab == null)
            SnaplogSession.get().setActiveTab( activeTab = Tab.DESKTOP );

        return activeTab.getTab().getPanel( wicketId );
    }

    /**
     * Generate some JavaScript to track a user hit on the given component in the analytics tracker.
     * 
     * @param trackComponent
     *            The component that we want to track a hit for.
     * @return The JavaScript code that, when executed, will track the hit.
     */
    protected static String trackJS(Component trackComponent) {

        Map<String, Object> trackVariables = new HashMap<String, Object>();
        trackVariables.put( "pageView", trackComponent.getClass().getSimpleName() );

        JavaScriptTemplate trackJS = new JavaScriptTemplate(
                new PackagedTextTemplate( LayoutPage.class, "trackPage.js" ) );

        return trackJS.asString( trackVariables );
    }

}
