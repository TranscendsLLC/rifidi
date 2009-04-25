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

/**
 * Interface for a lock for use to perform statement-level locking.
 */
public interface ManagedLock
{
    /**
     * Acquire a lock.
     * @param statementLockFactory is the engine lock factory service that the lock can use for engine lock services
     */
    public void acquireLock(StatementLockFactory statementLockFactory);

    /**
     * Release a lock.
     * @param statementLockFactory is the engine lock factory service that the lock can use for engine lock services
     */
    public void releaseLock(StatementLockFactory statementLockFactory);

    /**
     * Returns true if the current thread holds the lock, or false if not.
     * @return thread owner indication
     */
    public boolean isHeldByCurrentThread();
}
