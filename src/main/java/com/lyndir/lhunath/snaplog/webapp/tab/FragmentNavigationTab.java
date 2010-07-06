package com.lyndir.lhunath.snaplog.webapp.tab;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link FragmentNavigationTab}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentNavigationTab<P extends Panel> extends ITab {

    /**
     * @return The string that identifies this tab when it's the zero'th argument in the fragment part of the URL.
     */
    String getFragment();

    /**
     * Obtain the tab fragment and state arguments that would restore the state of the given panel of this tab.
     *
     * @param panel The panel for this tab; guaranteed of the type that was returned from #getPanel(String).
     *
     * @return All fragment arguments, the first being the same as #getFragment, all subsequent in the order that #applyFragmentState(Panel,
     *         String...) would take them to restore the given panel's state in another session.
     */
    Iterable<String> getFragmentState(Panel panel);

    /**
     * Apply fragment state specific to this tab.
     *
     * @param panel     The panel for this tab; guaranteed of the type that was returned from #getPanel(String)
     * @param arguments An array of arguments passed in the fragment part of the URL.  Excludes the tab fragment.
     */
    void applyFragmentState(Panel panel, String... arguments);

    /**
     * @param panelId The wicket ID to bind the panel to.
     *
     * @return The panel that serves this tab's contents.
     */
    @Override
    P getPanel(String panelId);

    /**
     * @return The class of the panel that will serve this tab's content.
     *
     * @see #getPanel(String)
     */
    Class<P> getPanelClass();
}
