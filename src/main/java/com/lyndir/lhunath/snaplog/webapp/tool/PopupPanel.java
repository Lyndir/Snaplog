package com.lyndir.lhunath.snaplog.webapp.tool;

import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link PopupPanel}<br> <sub>Popup that manages media access rights.</sub></h2>
 *
 * <p> <i>Jan 4, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class PopupPanel<M> extends GenericPanel<M> {

    private WebMarkupContainer content;

    /**
     * @param id    Wicket component ID.
     * @param model The panel's model.
     */
    protected PopupPanel(final String id, final IModel<M> model) {

        super( id, model );

        add( new WebMarkupContainer( "popup" ) {
            {
                add( content = new WebMarkupContainer( "content" ) {
                    {
                        setOutputMarkupId( true );
                    }} );
                initContent( content );
            }} );
    }

    /**
     * If the popup's content needs to be AJAX reloaded; add this component to the AJAX target.
     *
     * @return The container that the popup content should be added to.
     */
    protected WebMarkupContainer getContent() {

        return content;
    }

    /**
     * Add the popup content's components.
     *
     * @param content The component where popup content should be added to.
     */
    protected abstract void initContent(final WebMarkupContainer content);
}
