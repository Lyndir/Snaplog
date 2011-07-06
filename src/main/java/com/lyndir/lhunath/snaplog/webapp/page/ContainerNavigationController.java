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
public abstract class ContainerNavigationController extends TabController {

    static final Logger logger = Logger.get( ContainerNavigationController.class );

    @NotNull
    @Override
    @SuppressWarnings({ "unchecked" })
    protected <T extends TabDescriptor<P, ?>, P extends Panel> P getContent(@NotNull final T tab) {

        if (ObjectUtils.isEqual( tab, getActiveTab() ))
            return (P) getContainer().get( getContentId( tab ) );

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
    protected <T extends TabDescriptor<P, ?>, P extends Panel> void onTabActivated(@NotNull final T tab, @NotNull final P tabPanel) {

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

    protected abstract <T extends TabDescriptor<P, ?>, P extends Panel> String getContentId(@NotNull T tab);
}
