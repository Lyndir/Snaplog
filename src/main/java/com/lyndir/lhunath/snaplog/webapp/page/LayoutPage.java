package com.lyndir.lhunath.snaplog.webapp.page;

import static com.google.common.base.Preconditions.checkNotNull;
import net.link.safeonline.wicket.component.linkid.LinkIDLoginLink;

import org.apache.wicket.Component;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.lyndir.lhunath.lib.system.logging.Logger;
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

    private static final String TOOLS_PANEL = "toolsPanel";
    private static final String CONTENT_PANEL = "contentPanel";

    WebMarkupContainer userEntry;
    WebMarkupContainer userSummary;
    WebMarkupContainer tabsContainer;
    WebMarkupContainer toolbar;
    WebMarkupContainer tools;
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

                return !SnaplogSession.get().isAuthenticated();
            }
        };
        userEntry.add( new Label( "userGuessWelcome", getModelObject().userGuessWelcome() ) );
        userEntry.add( new LinkIDLoginLink( "userLogin" ) );

        // User Summary.
        userSummary = new WebMarkupContainer( "userSummary" ) {

            @Override
            public boolean isVisible() {

                return SnaplogSession.get().isAuthenticated();
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
                item.setVisible( itemModel.getObject().get().isVisible() );
            }
        };
        tabsContainer.add( headTabs );
        tabsContainer.setOutputMarkupId( true );

        // Toolbar.
        add( toolbar = new WebMarkupContainer( "toolbar" ) {

            IModel<Component> activeTools = new LoadableDetachableModel<Component>() {

                @Override
                protected Component load() {

                    Component toolsPanel = LayoutPageUtils.getActiveTab().get().getTools( TOOLS_PANEL );
                    if (toolsPanel == null)
                        toolsPanel = new WebMarkupContainer( TOOLS_PANEL ).setVisible( false );

                    return toolsPanel;
                }
            };

            {
                add( new Label( "focussedUser", getModelObject().focussedUser() ) );
                add( new Label( "focussedContent", getModelObject().focussedContent() ) );
            }


            @Override
            protected void onBeforeRender() {

                addOrReplace( activeTools.getObject() );

                super.onBeforeRender();
            }

            @Override
            public boolean isVisible() {

                return activeTools.getObject().isVisible() || getModelObject().focussedUser().getObject() != null;
            }
        } );

        // Page Content.
        add( (container = new WebMarkupContainer( "container" ) {

            @Override
            protected void onBeforeRender() {

                Panel activeContent = SnaplogSession.get().getActiveContent();
                if (activeContent == null)
                    activeContent = LayoutPageUtils.getActiveTab().get().getPanel( CONTENT_PANEL );
                addOrReplace( activeContent );

                add( new StringHeaderContributor( LayoutPageUtils.trackJS( activeContent ) ) );

                super.onBeforeRender();
            }
        }).setOutputMarkupId( true ) );
        SnaplogSession.get().setActiveContent( getInitialContentPanel( CONTENT_PANEL ) );

        add( pageTitle, userEntry, userSummary, tabsContainer );
    }

    /**
     * Override me to define a custom panel to show initially when this page is constructed.
     * 
     * @param wicketId
     *            The wicket ID that the panel should use.
     * @return The panel to show when the page first loads.
     */
    protected Panel getInitialContentPanel(@SuppressWarnings("unused") String wicketId) {

        return null;
    }

    /**
     * Reload the page using the given target.
     * 
     * @param target
     *            The target that will be servicing the reload response.
     */
    public void reloadFor(AjaxRequestTarget target) {

        checkNotNull( target, "Can't reload without a target." );

        target.addComponent( tabsContainer );
        target.addComponent( container );
    }
}
