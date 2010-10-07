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

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.lib.wayward.navigation.AbstractFragmentState;
import com.lyndir.lhunath.lib.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.security.Permission;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.error.TagUnavailableException;
import com.lyndir.lhunath.snaplog.error.UserNotFoundException;
import com.lyndir.lhunath.snaplog.model.service.*;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.tab.model.TagTabModels;
import com.lyndir.lhunath.snaplog.webapp.tool.*;
import com.lyndir.lhunath.snaplog.webapp.view.BrowserView;
import com.lyndir.lhunath.snaplog.webapp.view.FocusedView;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.*;


/**
 * <h2>{@link TagTabPanel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 1, 2010</i> </p>
 *
 * @author lhunath
 */
public class TagTabPanel extends GenericPanel<TagTabModels> {

    /**
     * Create a new {@link TagTabPanel} instance.
     *
     * @param id    The wicket ID that will hold the {@link TagTabPanel}.
     * @param model Provides the album to show.
     */
    TagTabPanel(final String id, final IModel<Tag> model) {

        super( id, new TagTabModels( model ).getModel() );

        add( new BrowserView( "browser", model ) {

            @Override
            public boolean isVisible() {

                return super.isVisible() && TagTabPanel.this.getModelObject().focusedMedia().getObject() == null;
            }
        } );
        add( new FocusedView( "focused", getModelObject().focusedMedia() ) );
    }

    interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link TagTabPanel}.
         */
        IModel<String> tabTitle();

        /**
         * @return Text on the "Back to Source" tool.
         */
        IModel<String> back();

        /**
         * @param mediaName The name of the media that couldn't be found.
         * @param source    The source in which we attempted to find the media.
         *
         * @return An error message explaining that a certain media was requested but couldn't be found in a certain source.
         */
        IModel<String> errorMediaNotFound(String mediaName, Source source);
    }


    /**
     * <h2>{@link TagTab}<br> <sub>The interface panel for browsing through the album content.</sub></h2>
     *
     * <p> <i>May 31, 2009</i> </p>
     *
     * @author lhunath
     */
    static class TagTab implements SnaplogTab<TagTabPanel, TagTabState> {

        public static final TagTab instance = new TagTab();

        static final Logger logger = Logger.get( TagTab.class );
        static final Messages msgs = MessagesFactory.create( Messages.class );

        /**
         * {@inheritDoc}
         */
        @Override
        public IModel<String> getTitle() {

            return msgs.tabTitle();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TagTabPanel newPanel(final String panelId) {

            return new TagTabPanel( panelId, Model.<Tag>of() );
        }

        @Override
        public Class<TagTabPanel> getPanelClass() {

            return TagTabPanel.class;
        }

        @Override
        public TagTabState getState(final String fragment) {

            return new TagTabState( fragment );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends SnaplogTool> listTools(final TagTabPanel panel) {

            return ImmutableList.of( new BackTool( panel.getModelObject() ), //
                                     new TimelinePopup.Tool( panel.getModelObject() ), //
                                     new AccessPopup.Tool( panel.getModelObject() ) //
            );
        }

        @Override
        public String getTabFragment() {

            return "album";
        }

        @Override
        public TagTabState buildFragmentState(final TagTabPanel panel) {

            Media focusedMedia = panel.getModelObject().focusedMedia().getObject();
            if (focusedMedia == null)
                return new TagTabState( panel.getModelObject().getModelObject() );

            return new TagTabState( panel.getModelObject().getModelObject(), focusedMedia );
        }

        @Override
        public void applyFragmentState(final TagTabPanel panel, final TagTabState state)
                throws IncompatibleStateException {

            try {
                logger.dbg( "Activating state: %s, on album tab.", state );
                SnaplogSession.get().setFocusedUser( state.getUser() );
                panel.getModelObject().setModelObject( state.getTag() );
                panel.getModelObject().focusedMedia().setObject( state.findMedia() );
                logger.dbg( "State is now: focused tag=%s, focused media=%s", panel.getModelObject().getModelObject(),
                            panel.getModelObject().focusedMedia().getObject() );
            }

            catch (UserNotFoundException e) {
                throw new IncompatibleStateException( e );
            }
            catch (TagUnavailableException e) {
                throw new IncompatibleStateException( e );
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isVisible() {

            return false;
        }
    }


    static class BackTool implements SnaplogLinkTool {

        static final Messages msgs = MessagesFactory.create( Messages.class );
        static final Logger logger = Logger.get( BackTool.class );

        private final TagTabModels model;

        BackTool(final TagTabModels model) {

            this.model = model;
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {

            // TODO: Figure out why state change isn't triggering tabContainer and contentContainer to get updated on page.
            model.focusedMedia().setObject( null );
        }

        @Override
        public IModel<String> getTitle() {

            return msgs.back();
        }

        @Override
        public IModel<String> getTitleClass() {

            return new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {

                    return "ss_sprite ss_application_view_tile";
                }
            };
        }

        @Override
        public boolean isVisible() {

            return model.focusedMedia().getObject() != null && model.getObject() != null && GuiceContext.getInstance(
                    SecurityService.class ).hasAccess( Permission.VIEW, SnaplogSession.get().newToken(), model.getObject() );
        }
    }


    public static class TagTabState extends AbstractFragmentState {

        static final Logger logger = Logger.get( TagTabState.class );

        private final UserService userService = GuiceContext.getInstance( UserService.class );
        private final SourceService<?, ?> sourceService = GuiceContext.getInstance( SourceService.class );
        private final TagService tagService = GuiceContext.getInstance( TagService.class );

        final String userName;
        final String tagName;
        final String mediaName;

        public TagTabState() {

            userName = null;
            tagName = null;
            mediaName = null;
        }

        public TagTabState(final String fragment) {

            super( fragment );

            // Load fields from fragments.
            userName = findFragment( 1 );
            tagName = findFragment( 2 );
            mediaName = findFragment( 3 );
        }

        public TagTabState(final Tag tag) {

            checkNotNull( tag, "Tag can't be null when creating state based on it." );

            // Load fields and fragments from parameter.
            appendFragment( userName = tag.getOwner().getUserName() );
            appendFragment( tagName = tag.getName() );
            appendFragment( mediaName = null );
        }

        public TagTabState(final Tag tag, final Media media) {

            checkNotNull( tag, "Tag can't be null when creating state based on it." );
            checkNotNull( media, "Media can't be null when creating state based on it." );
            checkArgument( ObjectUtils.equal( tag.getOwner(), media.getOwner() ), "Tag is not owned by media owner." );

            // Load fields and fragments from parameter.
            appendFragment( userName = tag.getOwner().getUserName() );
            appendFragment( tagName = tag.getName() );
            appendFragment( mediaName = media.getName() );
        }

        public User getUser()
                throws UserNotFoundException {

            return userService.getUserWithUserName( checkNotNull( userName, "Username must not be null in this state." ) );
        }

        public Tag getTag()
                throws TagUnavailableException, UserNotFoundException {

            User user = getUser();
            Tag tag = tagService.findTagWithName( SnaplogSession.get().newToken(), user, tagName );
            if (tag == null)
                throw new TagUnavailableException( user, tagName );

            return tag;
        }

        public Media findMedia()
                throws UserNotFoundException {

            return mediaName == null? null: sourceService.findMediaWithName( SnaplogSession.get().newToken(), getUser(), mediaName );
        }

        @Override
        protected String getTabFragment() {

            return TagTab.instance.getTabFragment();
        }
    }
}
