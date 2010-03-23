package com.lyndir.lhunath.snaplog.webapp.page;

import static com.google.common.base.Preconditions.checkState;
import net.link.safeonline.wicket.component.linkid.LinkIDLoginLink;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.StringHeaderContributor;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.SafeObjects;
import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.lib.wayward.component.AjaxLabelLink;
import com.lyndir.lhunath.lib.wayward.component.GenericWebPage;
import com.lyndir.lhunath.lib.wayward.component.LabelLink;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels.TabItem;
import com.lyndir.lhunath.snaplog.webapp.page.util.LayoutPageUtils;


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

    protected final Logger logger = Logger.get( getClass() );

    /**
     * The wicket ID that the content panel should have.
     * 
     * @see #setContentPanel(Panel, AjaxRequestTarget)
     */
    public static final String CONTENT_PANEL = "contentPanel";

    WebMarkupContainer userEntry;
    WebMarkupContainer userSummary;
    WebMarkupContainer tabsContainer;
    WebMarkupContainer container;


    /**
     * Create a new {@link LayoutPage}.
     */
    public LayoutPage() {

        super( new LayoutPageModels().getModel() );

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

                replaceComponentTagBody( markupStream, openTag, SnaplogSession.get().getActiveUser().getUserName() );
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
        userSummary.add( new Link<Object>( "userLogout" ) {

            @Override
            public void onClick() {

                Session.get().invalidate();
                throw new RestartResponseException( LayoutPage.class );
            }
        } );

        // Page Tab.
        tabsContainer = new WebMarkupContainer( "tabsContainer" );
        ListView<TabItem> headTabs = new ListView<TabItem>( "tabs", getModelObject().tabs() ) {

            @Override
            protected void populateItem(ListItem<TabItem> item) {

                final TabItem itemModel = item.getModelObject();

                item.add( new AjaxLabelLink( "link", itemModel.title() ) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        // TAB click.
                        LayoutPageUtils.setActiveTab( itemModel.getObject(), null );
                    }
                } );
                item.add( CSSClassAttributeAppender.ofString( item.getModelObject().styleClass() ) );
                item.setVisible( itemModel.getObject().getTab().isVisible() );
            }
        };
        tabsContainer.add( headTabs );
        tabsContainer.setOutputMarkupId( true );

        // Page Content.
        add( (container = new WebMarkupContainer( "container" ) {

            @Override
            protected void onBeforeRender() {

                add( new StringHeaderContributor( LayoutPageUtils.trackJS( get( CONTENT_PANEL ) ) ) );

                super.onBeforeRender();
            }
        }).setOutputMarkupId( true ) );
        setContentPanel( getInitialContentPanel( CONTENT_PANEL ), null );

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

        return LayoutPageUtils.getActiveTabPanel( wicketId );
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
     * @param target
     *            Optional AJAX request target. If specified, the components that need to be reloaded to update the page
     *            appropriately will be added to the target.
     */
    public void setContentPanel(Panel contentPanel, AjaxRequestTarget target) {

        checkState( SafeObjects.equal( contentPanel.getId(), CONTENT_PANEL ) );

        logger.dbg( "Setting content panel to: %s", contentPanel.getClass() );
        container.addOrReplace( contentPanel );

        if (target != null) {
            // We're in an AJAX request; add the components that need updating to it.
            target.addComponent( container );
            target.addComponent( tabsContainer );
        }
    }
}
