package com.lyndir.lhunath.snaplog.webapp.page;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.system.util.Throw;
import com.lyndir.lhunath.opal.wayward.navigation.*;
import java.lang.reflect.InvocationTargetException;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;


/**
 * <i>06 20, 2011</i>
 *
 * @author lhunath
 */
public abstract class ContainerNavigationController<P extends Panel, S extends FragmentState>
        extends FragmentNavigationListener.Controller<P, S> {

    static final Logger logger = Logger.get( ContainerNavigationController.class );

    @NotNull
    @Override
    @SuppressWarnings({ "unchecked" })
    protected <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> PP getContent(@NotNull final TT tab) {

        if (ObjectUtils.isEqual( tab, (FragmentNavigationTab<PP, SS>) getActiveTab() ))
            return (PP) getContainer().get( getContentId( tab ) );

        try {
            return tab.getContentPanelClass().getConstructor( String.class ).newInstance( getContentId( tab ) );
        }
        catch (InstantiationException e) {
            throw logger.bug( e );
        }
        catch (IllegalAccessException e) {
            throw logger.bug( e );
        }
        catch (InvocationTargetException e) {
            throw Throw.propagate( e );
        }
        catch (NoSuchMethodException e) {
            throw logger.bug( e );
        }
    }

    @Override
    protected <TT extends FragmentNavigationTab<?, ?>> void onTabActivated(@NotNull final TT tab, @NotNull final Panel tabPanel) {

        getContainer().addOrReplace( tabPanel );
    }

    @NotNull
    @Override
    protected Iterable<? extends Component> getNavigationComponents() {

        return ImmutableList.of( getContainer() );
    }

    @Override
    protected void onError(@NotNull final IncompatibleStateException e) {

        getContainer().error( e.getLocalizedMessage() );
    }

    protected abstract WebMarkupContainer getContainer();

    protected abstract <TT extends FragmentNavigationTab<PP, ?>, PP extends P> String getContentId(@NotNull TT tab);
}
