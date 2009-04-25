/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.dispatch;

import com.espertech.esper.collection.ArrayDequeJDK6Backport;
import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements dispatch service using a thread-local linked list of Dispatchable instances.
 */
public class DispatchServiceImpl implements DispatchService
{
    private static final ThreadLocal<ArrayDequeJDK6Backport<Dispatchable>> threadDispatchQueue = new ThreadLocal<ArrayDequeJDK6Backport<Dispatchable>>()
    {
        protected synchronized ArrayDequeJDK6Backport<Dispatchable> initialValue()
        {
            return new ArrayDequeJDK6Backport<Dispatchable>();
        }
    };

    public void dispatch()
    {
        dispatchFromQueue(threadDispatchQueue.get());
    }

    public void addExternal(Dispatchable dispatchable)
    {
        ArrayDequeJDK6Backport<Dispatchable> dispatchQueue = threadDispatchQueue.get();
        addToQueue(dispatchable, dispatchQueue);
    }

    private static void addToQueue(Dispatchable dispatchable, ArrayDequeJDK6Backport<Dispatchable> dispatchQueue)
    {
        dispatchQueue.add(dispatchable);
    }

    private static void dispatchFromQueue(ArrayDequeJDK6Backport<Dispatchable> dispatchQueue)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()) && (ExecutionPathDebugLog.isTimerDebugEnabled))
        {
            log.debug(".dispatchFromQueue Dispatch queue is " + dispatchQueue.size() + " elements");
        }

        while(true)
        {
            Dispatchable next = dispatchQueue.poll();
            if (next != null)
            {
                next.execute();
            }
            else
            {
                break;
            }
        }
    }

    private static final Log log = LogFactory.getLog(DispatchServiceImpl.class);
}
