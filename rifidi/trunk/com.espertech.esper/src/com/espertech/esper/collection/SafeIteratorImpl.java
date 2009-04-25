/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.collection;

import com.espertech.esper.util.ManagedLock;
import com.espertech.esper.client.SafeIterator;

import java.util.Iterator;

/**
 * Implements the safe iterator. The class is passed a lock that is locked already, to release
 * when the close method closes the iterator.
 */
public class SafeIteratorImpl<E> implements SafeIterator<E>
{
    private final ManagedLock iteratorLock;
    private final Iterator<E> underlying;
    private boolean lockTaken;

    /**
     * Ctor.
     * @param iteratorLock for locking resources to safely-iterate over
     * @param underlying is the underlying iterator to protect
     */
    public SafeIteratorImpl(ManagedLock iteratorLock, Iterator<E> underlying)
    {
        this.iteratorLock = iteratorLock;
        this.underlying = underlying;
        this.lockTaken = true;
    }

    public boolean hasNext()
    {
        return underlying.hasNext();
    }

    public E next()
    {
        return underlying.next();
    }

    public void close()
    {
        if (lockTaken)
        {
            iteratorLock.releaseLock(null);
            lockTaken = false;
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Remove operation not supported");
    }
}
