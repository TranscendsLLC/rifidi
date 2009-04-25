/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.collection;

/**
 * Simple queue implementation based on a Linked List per thread.
 * Objects can be added to the queue tail or queue head.
 */
public class ThreadWorkQueue
{
    private static final ThreadLocal<ArrayDequeJDK6Backport<Object>> threadQueue = new ThreadLocal<ArrayDequeJDK6Backport<Object>>()
    {
        protected synchronized ArrayDequeJDK6Backport<Object> initialValue()
        {
            return new ArrayDequeJDK6Backport<Object>();
        }
    };

    /**
     * Adds event to the end of the event queue.
     * @param event to add
     */
    public static void add(Object event)
    {
        ArrayDequeJDK6Backport<Object> queue = threadQueue.get();
        queue.addLast(event);
    }

    /**
     * Adds event to the front of the queue.
     * @param event to add
     */
    protected static void addFront(Object event)
    {
        ArrayDequeJDK6Backport<Object> queue = threadQueue.get();
        queue.addFirst(event);
    }

    /**
     * Returns the next event to getSelectListEvents, or null if there are no more events.
     * @return next event to getSelectListEvents
     */
    public static Object next()
    {
        ArrayDequeJDK6Backport<Object> queue = threadQueue.get();
        return queue.poll();
    }

    /**
     * Returns an indicator whether the queue is empty.
     * @return true for empty, false for not empty
     */
    public static boolean isEmpty()
    {
        ArrayDequeJDK6Backport<Object> queue = threadQueue.get();
        return queue.isEmpty();
    }
}
