/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.util;

import com.espertech.esper.core.StatementLockFactory;

import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;

/**
 * Simple lock based on {@link ReentrantLock} that associates a name with the lock and traces locking and unlocking.
 */
public class ManagedLockImpl implements ManagedLock
{
    private final ReentrantLock lock;
    private final String name;

    /**
     * Ctor.
     * @param name of lock
     */
    public ManagedLockImpl(String name)
    {
        this.name = name;
        this.lock = new ReentrantLock();
    }

    /**
     * Lock.
     */
    public void acquireLock(StatementLockFactory statementLockFactory)
    {
        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.traceLock(ManagedReadWriteLock.ACQUIRE_TEXT + name, lock);
        }

        lock.lock();

        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.traceLock(ManagedReadWriteLock.ACQUIRED_TEXT + name, lock);
        }
    }

    /**
     * Unlock.
     */
    public void releaseLock(StatementLockFactory statementLockFactory)
    {
        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.traceLock(ManagedReadWriteLock.RELEASE_TEXT + name, lock);
        }

        lock.unlock();

        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.traceLock(ManagedReadWriteLock.RELEASED_TEXT + name, lock);
        }
    }

    public boolean isHeldByCurrentThread()
    {
        boolean isHeld = lock.isHeldByCurrentThread();
        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.traceLock("Held    " + name + " by current:" + isHeld, lock);
        }
        return isHeld;
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + " name=" + name + " lock=" + lock;
    }
}
