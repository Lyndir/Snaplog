package com.lyndir.lhunath.snaplog.webapp.view;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.lib.wayward.provider.AbstractListProvider;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.object.user.User;
import com.lyndir.lhunath.snaplog.model.service.TagService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link AbstractTagsView}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 23, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractTagsView extends DataView<Tag> {

    static final Logger logger = Logger.get( AbstractTagsView.class );

    @Inject
    TagService tagService;

    /**
     * Create a new {@link AbstractTagsView} instance.
     *
     * @param id            The wicket ID to bind this component on.
     * @param ownerUser     The model that provides the owner whose tags to enumerate.
     * @param tagsPerPage The maximum amount of tags to show at once before hiding the rest behind a pager.
     */
    protected AbstractTagsView(final String id, final IModel<User> ownerUser, final int tagsPerPage) {

        this( id, new IPredicate<Tag>() {

            @Override
            public boolean apply(final Tag input) {

                return ObjectUtils.equal( input.getOwner(), ownerUser.getObject() );
            }
        }, tagsPerPage );
    }

    /**
     * Create a new {@link AbstractTagsView} instance.
     *
     * @param id            The wicket ID to bind this component on.
     * @param predicate     The predicate that should evaluate to <code>true</code> for each tag to return.
     * @param tagsPerPage The maximum amount of tags to show at once before hiding the rest behind a pager.
     */
    protected AbstractTagsView(final String id, final IPredicate<Tag> predicate, final int tagsPerPage) {

        super( id, new AbstractListProvider<Tag>() {

            @Override
            protected List<Tag> load() {

                return ImmutableList.copyOf( GuiceContext.getInstance( TagService.class ) //
                                                     .iterateTags( SnaplogSession.get().newToken(), predicate ) );
            }
        }, tagsPerPage );
    }

    /**
     * @param tagModel The model that provides the tag whose cover to return.
     *
     * @return A new model that will provide the cover {@link Media} for the tag provided by the given model.
     */
    public IModel<Media> cover(final IModel<Tag> tagModel) {

        return new LoadableDetachableModel<Media>() {

            @Override
            protected Media load() {

                Iterator<Media> it = tagService.iterateMedia( SnaplogSession.get().newToken(), tagModel.getObject(), false );
                if (it.hasNext())
                    return it.next();

                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return getItemCount() > 0;
    }
}
