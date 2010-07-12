package com.lyndir.lhunath.snaplog.webapp.tool;

import java.io.Serializable;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link SnaplogTool}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 13, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SnaplogTool extends Serializable {

    /**
     * @return The title on the toolbar for this tool.
     */
    IModel<String> getTitle();

    /**
     * @return The CSS class to apply to the title.  Useful for setting an icon or so.
     */
    IModel<String> getTitleClass();

    /**
     * @return <code>true</code> when this tool should be available from the toolbar.
     */
    boolean isVisible();
}
