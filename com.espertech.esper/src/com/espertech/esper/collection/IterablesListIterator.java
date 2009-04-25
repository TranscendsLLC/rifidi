/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.collection;

import com.espertech.esper.client.EventBean;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator over a list of iterables.
 * The IterablesListIterator iterator takes a list of Iterable instances as a parameter. The iterator will
 * Start at the very first Iterable and obtain it's iterator. It then allows iteration over this first iterator
 * until that iterator returns no next value. Then the IterablesListIterator iterator will obtain the next iterable and iterate
 * over this next iterable's iterator until no more values can be obtained. This continues until the last Iterable
 * in the order of the list of Iterables.
 */
public final class IterablesListIterator implements Iterator<EventBean>
{
    private final Iterator<Iterable<EventBean>> listIterator;
    private Iterator<EventBean> currentIterator;

    /**
     * Constructor - takes a list of Iterable that supply the iterators to iterate over.
     * @param iterables is a list of Iterable instances for which iterators to iterator over
     */
    public IterablesListIterator(List<Iterable<EventBean>> iterables)
    {
        listIterator = iterables.iterator();

        nextIterable();
    }


    public EventBean next()
    {
        if (currentIterator == null)
        {
            throw new NoSuchElementException();
        }
        if (currentIterator.hasNext())
        {
            return currentIterator.next();
        }

        nextIterable();

        if (currentIterator == null)
        {
            throw new NoSuchElementException();
        }
        return currentIterator.next();
    }

    public boolean hasNext()
    {
        if (currentIterator == null)
        {
            return false;
        }

        if (currentIterator.hasNext())
        {
            return true;
        }

        nextIterable();

        if (currentIterator == null)
        {
            return false;
        }

        return true;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    private void nextIterable()
    {
        while(listIterator.hasNext())
        {
            Iterable<EventBean> iterable = listIterator.next();
            currentIterator = iterable.iterator();
            if (currentIterator.hasNext())
            {
                return;
            }
        }

        currentIterator = null;
    }
}


