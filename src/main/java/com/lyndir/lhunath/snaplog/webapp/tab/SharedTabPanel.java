/*
 *   Copyright 2009, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.webapp.tab;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.component.GenericPanel;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.wayward.navigation.AbstractTabState;
import com.lyndir.lhunath.opal.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.media.Media;
import com.lyndir.lhunath.snaplog.data.object.media.MediaMapping;
import com.lyndir.lhunath.snaplog.error.MediaMappingNotFoundException;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.impl.SourceDelegate;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.tab.model.SharedTabModels;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import com.lyndir.lhunath.snaplog.webapp.view.FocusedView;
import java.util.List;
import org.apache.wicket.model.*;
import org.jetbrains.annotations.NotNull;


/**
 * <h2>{@link SharedTabPanel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 1, 2010</i> </p>
 *
 * @author lhunath
 */
public class SharedTabPanel extends GenericPanel<SharedTabModels> {

    /**
     * Create a new {@link SharedTabPanel} instance.
     *
     * @param id    The wicket ID that will hold the {@link SharedTabPanel}.
     */
    SharedTabPanel(final String id) {

        super( id, new SharedTabModels( Model.<MediaMapping>of() ).getModel() );

        add( new FocusedView( "focused", new AbstractReadOnlyModel<Media>() {
            @Override
            public Media getObject() {

                return getModelObject().getObject();
            }
        } ) );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link SharedTabPanel}.
         */
        IModel<String> tabTitle();
    }


    /**
     * <h2>{@link SharedTabDescriptor}<br> <sub>The interface panel for viewing a shared media.</sub></h2>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class SharedTabDescriptor implements SnaplogTabDescriptor<SharedTabPanel, SharedTabState> {

        public static final SharedTabDescriptor instance = new SharedTabDescriptor();

        static final Logger logger = Logger.get( SharedTabDescriptor.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

        /**
         * {@inheritDoc}
         */
        @NotNull
        @Override
        public IModel<String> getTitle() {

            return msgs.tabTitle();
        }

        @NotNull
        @Override
        public Class<SharedTabPanel> getContentPanelClass() {

            return SharedTabPanel.class;
        }

        @NotNull
        @Override
        public SharedTabState newState(@NotNull final String fragment) {

            return new SharedTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends SnaplogTool> listTools(final SharedTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getFragment() {

            return "shared";
        }

        @NotNull
        @Override
        public SharedTabState newState(@NotNull final SharedTabPanel panel) {

            return new SharedTabState( panel.getModelObject().getObject() );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean shownInNavigation() {

            return false;
        }
    }


    public static class SharedTabState extends AbstractTabState<SharedTabPanel> {

        static final Logger logger = Logger.get( SharedTabState.class );

        private final SourceDelegate sourceDelegate = GuiceContext.getInstance( SourceDelegate.class );

        final String mapping;

        public SharedTabState() {

            mapping = null;
        }

        public SharedTabState(final String fragment) {

            super( fragment );

            // Load fields from fragments.
            mapping = findFragment( 1 );
        }

        public SharedTabState(final MediaMapping mapping) {

            checkNotNull( mapping, "MediaMapping can't be null when creating state based on it." );

            // Load fields and fragments from parameter.
            appendFragment( this.mapping = mapping.getMapping() );
        }

        public SharedTabState(final Media media)
                throws PermissionDeniedException {

            checkNotNull( media, "Media can't be null when creating state based on it." );

            // Load fields and fragments from parameter.
            appendFragment( mapping = sourceDelegate.newMapping( SnaplogSession.get().newToken(), media ).getMapping() );
        }

        public MediaMapping getMapping()
                throws MediaMappingNotFoundException {

            MediaMapping mediaMapping = sourceDelegate.findMediaMapping( SnaplogSession.get().newToken(),
                                                                       checkNotNull( mapping, "Mapping must not be null in this state." ) );
            if (mediaMapping == null)
                throw new MediaMappingNotFoundException( mapping );

            return mediaMapping;
        }

        @Override
        public void apply(@NotNull final SharedTabPanel panel)
                throws IncompatibleStateException {

            try {
                logger.dbg( "Activating state: %s, on view tab.", this );
                panel.getModelObject().setObject( getMapping() );
                logger.dbg( "State is now: mapping=%s", panel.getModelObject().getObject() );
            }

            catch (MediaMappingNotFoundException e) {
                throw new IncompatibleStateException( e );
            }
        }
    }
}
