/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventBean;

/**
 * The result of executing a prepared query.
 */
public class EPPreparedQueryResult
{
    private final EventType eventType;
    private final EventBean[] result;

    /**
     * Ctor.
     * @param eventType is the type of event produced by the query
     * @param result the result rows
     */
    public EPPreparedQueryResult(EventType eventType, EventBean[] result)
    {
        this.eventType = eventType;
        this.result = result;
    }

    /**
     * Returs the event type representing the selected columns.
     * @return metadata
     */
    public EventType getEventType()
    {
        return eventType;
    }

    /**
     * Returns the query result.
     * @return result rows
     */
    public EventBean[] getResult()
    {
        return result;
    }
}
