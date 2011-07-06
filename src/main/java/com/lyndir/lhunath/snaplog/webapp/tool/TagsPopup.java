package com.lyndir.lhunath.snaplog.webapp.tool;

import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.opal.security.service.SecurityService;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.Source;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link TagsPopup}<br> <sub>Popup that allows users to manage and navigate media tags.</sub></h2>
 *
 * <p> <i>Jan 4, 2010</i> </p>
 *
 * @author lhunath
 */
public class TagsPopup extends PopupPanel<Media> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    /**
     * @param id         The wicket ID of the tab.
     * @param mediaModel The {@link Source} whose {@link Media} to scan for tags.
     */
    public TagsPopup(final String id, final IModel<Media> mediaModel) {

        super( id, mediaModel );
    }

    @Override
    protected void initContent(final WebMarkupContainer content) {

    }

    @Override
    public boolean isVisible() {

        return new Tool( getModel() ).isVisible();
    }

    interface Messages {

        /**
         * @return The title of the toolbar tool that will activate the Timeline popup.
         */
        IModel<String> tool();
    }


    public static class Tool implements SnaplogPanelTool {

        private final IModel<Media> model;

        /**
         * @param model The model that provides the media whose access should be managed through this tool.
         */
        public Tool(final IModel<Media> model) {

            this.model = model;
        }

        @Override
        public IModel<String> getTitle() {

            return msgs.tool();
        }

        @Override
        public IModel<String> getTitleClass() {

            return new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {

                    return "ss_sprite ss_tag_green";
                }
            };
        }

        @Override
        public Panel getPanel(final String id) {

            return new TagsPopup( id, model );
        }

        @Override
        public boolean isVisible() {

            return model.getObject() != null && GuiceContext.getInstance( SecurityService.class )
                                                            .hasAccess( Permission.CONTRIBUTE, SnaplogSession.get().newToken(),
                                                                        model.getObject() );
        }
    }
}
