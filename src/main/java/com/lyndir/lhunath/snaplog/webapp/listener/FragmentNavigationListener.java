package com.lyndir.lhunath.snaplog.webapp.listener;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.lyndir.lhunath.lib.wayward.js.AjaxHooks;
import com.lyndir.lhunath.snaplog.webapp.tab.FragmentNavigationTab;
import java.net.URI;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link FragmentNavigationListener}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class FragmentNavigationListener implements AjaxHooks.IPageListener {

    @Override
    public void onReady(final AjaxRequestTarget target, final String pageUrl) {

        URI uri = URI.create( pageUrl );
        if (uri.getFragment() == null) {
            // No fragment, find and set a default tab.
            for (final FragmentNavigationTab<?> tab : getTabs()) {
                if (tab.isVisible()) {
                    activateTab( tab, tab.getPanel( getTabContentId() ), target );
                    break;
                }
            }
        } else {
            // There is a fragment, load state from it.
            Iterable<String> arguments = Splitter.on( '/' ).split( uri.getFragment() );
            String tabFragment = arguments.iterator().next();

            for (final FragmentNavigationTab<?> tab : getTabs()) {
                if (tab.getFragment().equalsIgnoreCase( tabFragment ) && tab.isVisible()) {
                    // Apply tab state from fragment.
                    Panel tabPanel = tab.getPanel( getTabContentId() );
                    tab.applyFragmentState( tabPanel, Iterables.toArray( arguments, String.class ) );

                    activateTab( tab, tabPanel, target );
                    break;
                }
            }
        }
    }

    private void activateTab(final FragmentNavigationTab<?> tab, Panel tabPanel, final AjaxRequestTarget target) {

        if (tabPanel == null)
            tabPanel = tab.getPanel( getTabContentId() );
        tabPanel.setOutputMarkupPlaceholderTag( true );

        if (target != null)
            target.addComponent( tabPanel );

        setActiveTab( tab, tabPanel, target );
    }

    /**
     * Invoked when a page is loaded to indicate the page's active tab as determined by fragment state.
     *
     * @param tab      The tab that needs to be activated.
     * @param tabPanel The panel that contains the tab's content as determined by fragment state.
     * @param target   The AJAX request that the fragment processing is happening in.  The tab's content panel has already been added to
     *                 this.
     */
    protected abstract void setActiveTab(final FragmentNavigationTab<?> tab, final Panel tabPanel, final AjaxRequestTarget target);

    /**
     * @return The wicket ID that the tab's content panel should bind to when generated to apply fragment state on it.
     */
    protected abstract String getTabContentId();

    /**
     * Note:   The order should reflect the defaulting preference.  When no tab is selected by the fragment (or there is no fragment), the
     * first tab will be used instead, if visible.  If not visible, the next one will be tried, and so on.
     *
     * @return The application's tabs.
     */
    protected abstract Iterable<FragmentNavigationTab<?>> getTabs();
}
