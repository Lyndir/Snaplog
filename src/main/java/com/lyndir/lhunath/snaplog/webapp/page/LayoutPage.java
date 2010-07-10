package com.lyndir.lhunath.snaplog.webapp.page;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.behavior.CSSClassAttributeAppender;
import com.lyndir.lhunath.lib.wayward.behavior.JSLink;
import com.lyndir.lhunath.lib.wayward.component.AjaxLabelLink;
import com.lyndir.lhunath.lib.wayward.component.GenericWebPage;
import com.lyndir.lhunath.lib.wayward.component.LabelLink;
import com.lyndir.lhunath.lib.wayward.i18n.KeyAppender;
import com.lyndir.lhunath.lib.wayward.i18n.KeyMatch;
import com.lyndir.lhunath.lib.wayward.js.AjaxHooks;
import com.lyndir.lhunath.lib.wayward.navigation.FragmentNavigationListener;
import com.lyndir.lhunath.lib.wayward.navigation.FragmentNavigationTab;
import com.lyndir.lhunath.lib.wayward.navigation.FragmentState;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels;
import com.lyndir.lhunath.snaplog.webapp.page.model.LayoutPageModels.TabItem;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.HashMap;
import java.util.List;
import net.link.safeonline.wicket.component.linkid.LinkIDLoginLink;
import org.apache.wicket.*;
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

    final FragmentNavigationController navigationController = new FragmentNavigationController();

    final WebMarkupContainer userEntry;
    final WebMarkupContainer userSummary;
    final WebMarkupContainer tabsContainer;
    final WebMarkupContainer contentContainer;
    final WebMarkupContainer messages;

    final HashMap<SnaplogTool, Panel> toolPanels;
    final LoadableDetachableModel<List<? extends SnaplogTool>> tools;

    /**
     * Create a new {@link LayoutPage}.
     */
    public LayoutPage() {

        super( new LayoutPageModels().getModel() );
        getModelObject().attach( this );

        // Ajax Hooks
        AjaxHooks.installAjaxEvents( this );
        AjaxHooks.installPageEvents( this, new FragmentNavigationListener.PageListener( navigationController ) );

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
        tabsContainer = new WebMarkupContainer( "tabsContainer" );
        ListView<TabItem> headTabs = new ListView<TabItem>( "tabs", getModelObject().tabs() ) {

            @Override
            protected void populateItem(final ListItem<TabItem> item) {

                final TabItem itemModel = item.getModelObject();

                item.add( new AjaxLabelLink( "link", itemModel.title() ) {

                    @Override
                    public void onClick(final AjaxRequestTarget target) {

                        itemModel.getObject().activate();
                    }
                } );
                item.add( CSSClassAttributeAppender.ofString( item.getModelObject().styleClass() ) );
                item.setVisible( itemModel.getObject().get().isVisible() );
            }
        };
        tabsContainer.add( headTabs );
        tabsContainer.setOutputMarkupId( true );

        // Toolbar.
        toolPanels = new HashMap<SnaplogTool, Panel>();
        tools = new LoadableDetachableModel<List<? extends SnaplogTool>>() {

            @Override
            protected List<? extends SnaplogTool> load() {

                toolPanels.clear();
                Tab activeTab = getModelObject().activeTab().getObject();
                if (activeTab == null)
                    return ImmutableList.of();

                // Load the panels for the tools and assign them a markup ID.
                List<? extends SnaplogTool> activeTools = activeTab.get().listTools();
                for (final SnaplogTool tool : activeTools) {
                    Panel panel = tool.getPanel( "panel" );
                    panel.setOutputMarkupId( true );
                    toolPanels.put( tool, panel );
                }

                return activeTools;
            }
        };

        add( new ListView<SnaplogTool>( "tools", tools ) {

            @Override
            protected void populateItem(final ListItem<SnaplogTool> item) {

                SnaplogTool tool = item.getModelObject();
                item.add( new Label( "link", tool.getTitle() ).add( CSSClassAttributeAppender.of( tool.getTitleClass() ) ).add(
                        new JSLink( "popup", toolPanels.get( tool ).getMarkupId(), "toggle" ) ) );
                item.setVisible( tool.isVisible() );
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
            }} );

        // Page Content.
        add( (contentContainer = new WebMarkupContainer( "contentContainer" ) {

            {
                add( new ListView<SnaplogTool>( "toolPanels", tools ) {
                    @Override
                    protected void populateItem(final ListItem<SnaplogTool> item) {

                        SnaplogTool tool = item.getModelObject();
                        item.add( toolPanels.get( tool ).setVisible( tool.isVisible() ) );
                    }
                } );
                add( getInitialContent( CONTENT_PANEL ) );
            }}).setMarkupId( "content" /* TODO: Wicket should REALLY dig this out of the markup! */ ).setOutputMarkupId( true ) );

        add( pageTitle, userEntry, userSummary, tabsContainer );
    }

    /**
     * Override me to define a custom panel to show initially when this page is constructed.
     *
     * @param wicketId The wicket ID that the panel should use.
     *
     * @return The panel to show when the page first loads.
     */
    protected Component getInitialContent(final String wicketId) {

        return new WebComponent( wicketId );
    }

    /**
     * <b>Note:</b> This method may only be invoked when this page is currently active.
     *
     * @return The fragment navigation controller that manages this page.
     */
    public static FragmentNavigationController getController() {

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

        checkNotNull( target, "Given target cannot be null." );

        target.addComponent( messages );
        target.addListener( new FragmentNavigationListener.AjaxRequestListener() {
            @Override
            protected FragmentNavigationTab<?, ? extends FragmentState<?, ?>> getActiveTab() {

                Tab activeTab = getModelObject().activeTab().getObject();
                return activeTab == null? null: activeTab.get();
            }

            @Override
            protected Component getActiveContent() {

                return contentContainer.get( CONTENT_PANEL );
            }
        } );
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
         * @param albumOwnerBadge The badge character of the owner of the currently viewed album.
         * @param albumOwnerName  The name of the owner of the currently viewed album.
         *
         * @return Text that will go in the page's title.
         */
        String pageTitle(char albumOwnerBadge, String albumOwnerName);

        /**
         * @param userBadge The badge character of the logged-in user.
         * @param userName  The name of the logged-in user.
         *
         * @return Welcoming text greeting the logged-in user.
         */
        String userWelcome(char userBadge, String userName);

        /**
         * @param userBadge The badge of the user we guess is using the page.
         * @param userName  The name of the user we guess is using the page.
         *
         * @return Welcoming the user back. The user has not yet authenticated himself. The identification is just a guess.
         */
        String userWelcomeBack(char userBadge, String userName);

        /**
         * @return The designation of a user who we can't identify.
         */
        String userNameUnknown();

        /**
         * @param messageCount The amount of messages the user has.
         *
         * @return Text indicating the user has messages.
         */
        String userMessages(@KeyAppender(value = @KeyMatch(ifNum = 1, key = "singular", elseKey = "plural"), useValue = true)//
                int messageCount);

        /**
         * @param requestCount The amount of pending requests.
         *
         * @return Text indicating there are pending requests for the active user.
         */
        String userRequests(@KeyAppender(value = @KeyMatch(ifNum = 1, key = "singular", elseKey = "plural"), useValue = true)//
                int requestCount);

        /**
         * @param userBadge The focused user's badge.
         * @param userName  The focused user's userName.
         *
         * @return A text indicating that the given user is the one currently focusing on.
         */
        String focusedUser(char userBadge, String userName);

        /**
         * @param albumName The name of the album that's being focused on.
         *
         * @return A text indicating what the user's currently focusing on.
         */
        // TODO: If we want to allow focusing other content; this may need improvement. If not, this may be simplified?
        String focusedContent(@KeyAppender(nullKey = "none", notNullKey = "album", useValue = true)//
                String albumName);
    }


    public class FragmentNavigationController extends FragmentNavigationListener.Controller {

        @Override
        protected void setActiveTab(final FragmentNavigationTab<?, ?> tab, final Panel tabPanel) {

            getModelObject().activeTab().setObject( Tab.of( tab ) );

            Panel contentPanel = tabPanel;
            if (contentPanel == null)
                contentPanel = tab.getPanel( CONTENT_PANEL );
            contentContainer.addOrReplace( contentPanel );

            AjaxRequestTarget target = AjaxRequestTarget.get();
            if (target != null) {
                target.addComponent( tabsContainer );
                target.addComponent( contentContainer );
            }
        }

        @Override
        protected String getTabContentId() {

            return CONTENT_PANEL;
        }

        @Override
        protected Iterable<FragmentNavigationTab<?, ?>> getTabs() {

            ImmutableList.Builder<FragmentNavigationTab<?, ?>> tabsBuilder = ImmutableList.builder();
            for (final Tab tab : Tab.values())
                tabsBuilder.add( tab.get() );

            return tabsBuilder.build();
        }
    }
}
