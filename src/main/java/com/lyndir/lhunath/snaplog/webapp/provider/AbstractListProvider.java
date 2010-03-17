package com.lyndir.lhunath.snaplog.webapp.provider;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;


/**
 * <h2>{@link AbstractListProvider}<br>
 * <sub>Provides data from a lazy loaded detachable list.</sub></h2>
 * 
 * <p>
 * <i>Mar 7, 2010</i>
 * </p>
 * 
 * @param <T>
 *            The type of data that will be provided.
 * @author lhunath
 */
public abstract class AbstractListProvider<T> implements IDataProvider<T> {

    private List<T> source;


    private List<T> getSource() {

        if (source == null)
            source = loadSource();

        return source;
    }

    /**
     * @return The query that provides the data.
     */
    protected abstract List<T> loadSource();

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {

        source = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator(int first, int count) {

        return getSource().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {

        return getSource().size();
    }
}
