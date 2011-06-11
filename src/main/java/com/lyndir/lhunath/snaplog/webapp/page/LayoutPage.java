package com.lyndir.lhunath.snaplog.webapp.page;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.opal.wayward.behavior.JSLink;
import com.lyndir.lhunath.opal.wayward.component.AjaxLabelLink;
import com.lyndir.lhunath.opal.wayward.component.GenericWebPage;
import com.lyndir.lhunath.opal.wayward.component.LabelLink;
import com.lyndir.lhunath.opal.wayward.i18n.KeyAppender;
import com.lyndir.lhunath.opal.wayward.i18n.KeyMatch;
import com.lyndir.lhunath.opal.wayward.js.AjaxHooks;
import com.lyndir.lhunath.opal.wayward.navigation.FragmentNavigationListener;
import com.lyndir.lhunath.opal.wayward.navigation.FragmentNavigationTab;
import com.lyndir.lhunath.opal.wayward.navigation.FragmentState;
import com.lyndir.lhunath.opal.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels.TabItem;
import com.lyndir.lhunath.snaplog.webapp.tab.SnaplogTab;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogLinkTool;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogPanelTool;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.HashMap;
import java.util.List;
import net.link.safeonline.wicket.component.linkid.LinkIDLoginLink;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link LayoutPage}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Jan 28, 2010</i> </p>
 *
 * @author lhunath
 */
public class LayoutPage extends GenericWebPage<LayoutPageModels> implements IAjaxIndicatorAware {

    protected final Logger logger = Logger.get( getClass() );

    private static final String CONTENT_PANEL = "contentPanel";

    final SnaplogNavigationController<Panel, FragmentState> navigationController = new SnaplogNavigationController<Panel, FragmentState>();

    final WebMarkupContainer userEntry;
    final WebMarkupContainer userSummary;
    final WebMarkupContainer tabsContainer;
    final WebMarkupContainer contentContainer;
    final WebMarkupContainer messages;

    final HashMap<SnaplogPanelTool, Panel> toolPanels;
    final LoadableDetachableModel<List<? extends SnaplogTool>> tools;

    /**
     * Create a new {@link LayoutPage}.
     */
    public LayoutPage() {

        super( new LayoutPageModels().getModel() );
        getModelObject().attach( this );

        // Ajax Hooks
        AjaxHooks.installAjaxEvents( this );
        AjaxHooks.installPageEvents( this, FragmentNavigationListener.PageListener.of( navigationController ) );

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
            protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

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
        ListView<TabItem> headTabs = new ListView<TabItem>( "tabs", getModelObject().tabs() ) {

            @Override
            protected void populateItem(final ListItem<TabItem> item) {

                final TabItem itemModel = item.getModelObject();

                item.add( new AjaxLabelLink( "link", itemModel.title() ) {

                    @Override
                    public void onClick(final AjaxRequestTarget target) {

                        itemModel.getObject().activateNew();
                    }
                } );
                item.add( CSSClassAttributeAppender.ofString( item.getModelObject().styleClass() ) );
                item.setVisible( itemModel.getObject().isVisible() );
            }
        };

        // Toolbar.
        toolPanels = new HashMap<SnaplogPanelTool, Panel>();
        tools = new LoadableDetachableModel<List<? extends SnaplogTool>>() {

            @Override
            protected List<? extends SnaplogTool> load() {

                toolPanels.clear();

                // Suppress warning & cast later on because we don't know the correct type of the content panel here.

                @SuppressWarnings({ "unchecked" })
                SnaplogTab<Panel, ? extends FragmentState> activeTab = (SnaplogTab<Panel, ? extends FragmentState>) getController().getActiveTab();
                if (activeTab == null)
                    return ImmutableList.of();

                // Load the panels for the tools and assign them a markup ID.
                Panel tabPanel = (Panel) contentContainer.get( CONTENT_PANEL );
                List<? extends SnaplogTool> activeTools = activeTab.listTools( tabPanel );
                for (final SnaplogTool tool : activeTools) {
                    if (tool instanceof SnaplogPanelTool) {
                        Panel panel = ((SnaplogPanelTool) tool).getPanel( "panel" );
                        panel.setOutputMarkupId( true );

                        toolPanels.put( (SnaplogPanelTool) tool, panel );
                    }
                }

                return activeTools;
            }
        };

        tabsContainer = new WebMarkupContainer( "tabsContainer" );
        tabsContainer.setOutputMarkupId( true );
        tabsContainer.add( headTabs, new ListView<SnaplogTool>( "tools", tools ) {

            @Override
            protected void populateItem(final ListItem<SnaplogTool> item) {

                SnaplogTool tool = item.getModelObject();
                Component link = new Label( "link", tool.getTitle() ).add( CSSClassAttributeAppender.of( tool.getTitleClass() ) );

                // Add tool-type specific behaviour.
                if (tool instanceof SnaplogPanelTool) {
                    SnaplogPanelTool panelTool = (SnaplogPanelTool) tool;
                    link.add( new JSLink( "popup", toolPanels.get( panelTool ).getMarkupId(), "toggle" ) );
                } else if (tool instanceof SnaplogLinkTool) {
                    final SnaplogLinkTool linkTool = (SnaplogLinkTool) tool;
                    link.add( new AjaxEventBehavior( "onClick" ) {

                        @Override
                        protected void onEvent(final AjaxRequestTarget target) {

                            linkTool.onClick( target );
                        }
                    } );
                }

                item.add( link ).setVisible( tool.isVisible() );
            }

            @Override
            public boolean isVisible() {

                for (final SnaplogTool tool : getList())
                    if (tool.isVisible())
                        return true;

                return false;
            }
        } );

        // Global Messages.
        add( messages = new WebMarkupContainer( "messages" ) {

            {
                setOutputMarkupId( true );

                add( new FeedbackPanel( "errors", new IFeedbackMessageFilter() {

                    @Override
                    public boolean accept(final FeedbackMessage message) {

                        return message.getLevel() >= FeedbackMessage.WARNING;
                    }
                } ) {

                    @Override
                    public boolean isVisible() {

                        return anyMessage();
                    }
                } );
                add( new FeedbackPanel( "infos", new IFeedbackMessageFilter() {

                    @Override
                    public boolean accept(final FeedbackMessage message) {

                        return message.getLevel() <= FeedbackMessage.INFO;
                    }
                } ) {

                    @Override
                    public boolean isVisible() {

                        return anyMessage();
                    }
                } );
            }
        } );

        // Page Content.
        add( (contentContainer = new WebMarkupContainer( "contentContainer" ) {

            {
                add( new ListView<SnaplogPanelTool>( "toolPanels", new LoadableDetachableModel<List<? extends SnaplogPanelTool>>() {

                    @Override
                    protected List<? extends SnaplogPanelTool> load() {

                        return ImmutableList.copyOf( toolPanels.keySet() );
                    }
                } ) {

                    @Override
                    protected void populateItem(final ListItem<SnaplogPanelTool> item) {

                        SnaplogPanelTool tool = item.getModelObject();
                        item.add( toolPanels.get( tool ).setVisible( tool.isVisible() ) );
                    }
                } );
                add( new WebComponent( CONTENT_PANEL ) );
            }
        }).setMarkupId( "content" /* TODO: Wicket should REALLY dig this out of the markup! */ ).setOutputMarkupId( true ) );

        add( pageTitle, userEntry, userSummary, tabsContainer );
    }

    @Override
    protected void onBeforeRender() {

        // FIXME: Ugly hack
        contentContainer.get( CONTENT_PANEL ).setVisibilityAllowed( false );

        super.onBeforeRender();
    }

    /**
     * <b>Note:</b> This method may only be invoked when this page is currently active.
     *
     * @return The fragment navigation controller that manages this page.
     */
    public static SnaplogNavigationController<Panel, FragmentState> getController() {

        Page responsePage = RequestCycle.get().getResponsePage();
        checkState( LayoutPage.class.isInstance( responsePage ), //
                    "Can't access LayoutPage's controller, while it isn't the response page.  Response page is: %s.", responsePage );

        return ((LayoutPage) responsePage).navigationController;
    }

    /**
     * Add components to the AJAX target that should be reloaded during every AJAX event on this page.
     *
     * @param target The AJAX request target to add page components to.
     */
    public void addComponents(final AjaxRequestTarget target) {

        // FIXME: Ugly hack
        contentContainer.get( CONTENT_PANEL ).setVisibilityAllowed( true );

        target.addComponent( messages );
        target.addListener( FragmentNavigationListener.AjaxRequestListener.of( navigationController ) );
    }

    @Override
    public String getAjaxIndicatorMarkupId() {

        return "headerIndicator";
    }

    /**
     * <h2>{@link Messages}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> <i>Mar 31, 2010</i> </p>
     *
     * @author lhunath
     */
    public interface Messages {

        /**
         * @return Text that will go in the page's title.
         */
        IModel<String> pageTitle();

        /**
         * @param userName The name of the logged-in user.
         *
         * @return Welcoming text greeting the logged-in user.
         */
        IModel<String> userWelcome(
                @KeyAppender(nullKey = "unknownUser", notNullKey = "knownUser", useValue = true) IModel<String> userName);

        /**
         * @param messageCount The amount of messages the user has.
         *
         * @return Text indicating the user has messages.
         */
        IModel<String> userMessages(@KeyAppender(value = @KeyMatch(ifNum = 1, key = "singular", elseKey = "plural"), useValue = true)//
                                            int messageCount);

        /**
         * @param requestCount The amount of pending requests.
         *
         * @return Text indicating there are pending requests for the active user.
         */
        IModel<String> userRequests(@KeyAppender(value = @KeyMatch(ifNum = 1, key = "singular", elseKey = "plural"), useValue = true)//
                                            int requestCount);

        /**
         * @param user The focused user.
         *
         * @return A text indicating that the given user is the one currently focusing on.
         */
        IModel<String> focusedUser(IModel<User> user);

        /**
         * @param tag The tag that's being focused on.
         *
         * @return A text indicating what the user's currently focusing on.
         */
        // TODO: If we want to allow focusing other content; this may need improvement. If not, this may be simplified?
        IModel<String> focusedContent(@KeyAppender(nullKey = "none", notNullKey = "tag", useValue = true) //
                                              IModel<Source> tag);
    }


    public class SnaplogNavigationController<P extends Panel, S extends FragmentState>
            extends FragmentNavigationListener.Controller<P, S, SnaplogTab<? extends P, ? extends S>> {

        @Override
        protected Class<? extends Page> getTabExclusivePage() {

            return LayoutPage.class;
        }

        @Override
        protected Component getActiveContent() {

            return contentContainer.get( CONTENT_PANEL );
        }

        @Override
        protected <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void setActiveTab(final TT tab,
                                                                                                           final Panel tabPanel) {

            Panel contentPanel = tabPanel;
            if (contentPanel == null)
                contentPanel = tab.newPanel( CONTENT_PANEL );

            contentContainer.addOrReplace( contentPanel );
        }

        @Override
        protected Iterable<? extends Component> getNavigationComponents() {

            return ImmutableList.of( tabsContainer, contentContainer );
        }

        @Override
        protected String getTabContentId() {

            return CONTENT_PANEL;
        }

        @Override
        protected Iterable<SnaplogTab<? extends P, ? extends S>> getTabs() {

            ImmutableList.Builder<SnaplogTab<? extends P, ? extends S>> tabsBuilder = ImmutableList.builder();
            for (final Tab tab : Tab.values()) {
                SnaplogTab<? extends P, ? extends S> snaplogTab = tab.get();
                tabsBuilder.add( snaplogTab );
            }

            return tabsBuilder.build();
        }

        @Override
        protected void onError(final IncompatibleStateException e) {

            logger.err( e, "While navigating" );

            error( e.getLocalizedMessage() );
        }
    }
}
