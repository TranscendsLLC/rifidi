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
import com.espertech.esper.client.EventType;

import java.util.Iterator;

/**
 * Results of an on-demand (fire-and-forget non-continuous) query.
 */
public interface EPOnDemandQueryResult
{
    /**
     * Returns an array representing query result rows.
     * @return result array
     */
    public EventBean[] getArray();

    /**
     * Returns the event type of the result.
     * @return event type of result row
     */
    public EventType getEventType();

    /**
     * Returns an iterator representing query result rows.
     * @return result row iterator
     */
    public Iterator<EventBean> iterator();
}
