/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

import com.espertech.esper.client.EventBean;

/**
 * Defines an interface to notify of new and old events.
 * <p>
 * Also see {@link StatementAwareUpdateListener} for update listeners that require
 * the statement and service provider instance to be passed to the listener in addition to events. 
 */
public interface UpdateListener
{
    /**
     * Notify that new events are available or old events are removed.
     * If the call to update contains new (inserted) events, then the first argument will be a non-empty list and
     * the second will be empty. Similarly, if the call is a notification of deleted events, then the first argument
     * will be empty and the second will be non-empty.
     *
     * Either the newEvents or oldEvents will be non-null. This method won't be called with both arguments being null,
     * but either one could be null. The same is true for zero-length arrays.
     * Either newEvents or oldEvents will be non-empty. If both are non-empty, then the update is a modification
     * notification.
     *
     * @param newEvents is any new events. This will be null or empty if the update is for old events only.
     * @param oldEvents is any old events. This will be null or empty if the update is for new events only.
     */
    public void update(EventBean[] newEvents, EventBean[] oldEvents);
}
