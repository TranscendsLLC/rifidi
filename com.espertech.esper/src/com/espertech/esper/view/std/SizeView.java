/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.std;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.espertech.esper.collection.SingleEventIterator;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.view.*;
import com.espertech.esper.core.StatementContext;

/**
 * This view is a very simple view presenting the number of elements in a stream or view.
 * The view computes a single long-typed count of the number of events passed through it similar
 * to the base statistics COUNT column.
 */
public final class SizeView extends ViewSupport implements CloneableView
{
    private final StatementContext statementContext;
    private EventType eventType;
    private long size = 0;
    private EventBean lastSizeEvent;

    /**
     * Ctor.
     * @param statementContext is services
     */
    public SizeView(StatementContext statementContext)
    {
        this.statementContext = statementContext;
        this.eventType = createEventType(statementContext);
    }

    public View cloneView(StatementContext statementContext)
    {
        return new SizeView(statementContext);
    }

    public final EventType getEventType()
    {
        return eventType;
    }

    public final void update(EventBean[] newData, EventBean[] oldData)
    {
        long priorSize = size;

        // add data points to the window
        if (newData != null)
        {
            size += newData.length;
        }

        if (oldData != null)
        {
            size -= oldData.length;
        }

        // If there are child views, fireStatementStopped update method
        if ((this.hasViews()) && (priorSize != size))
        {
            Map<String, Object> postNewData = new HashMap<String, Object>();
            postNewData.put(ViewFieldEnum.SIZE_VIEW__SIZE.getName(), size);
            EventBean newEvent = statementContext.getEventAdapterService().adaptorForTypedMap(postNewData, eventType);

            if (lastSizeEvent != null)
            {
                updateChildren(new EventBean[] {newEvent}, new EventBean[] {lastSizeEvent});
            }
            else
            {
                Map<String, Object> postOldData = new HashMap<String, Object>();
                postOldData.put(ViewFieldEnum.SIZE_VIEW__SIZE.getName(), priorSize);
                EventBean oldEvent = statementContext.getEventAdapterService().adaptorForTypedMap(postOldData, eventType);

                updateChildren(new EventBean[] {newEvent}, new EventBean[] {oldEvent});
            }

            lastSizeEvent = newEvent;
        }
    }

    public final Iterator<EventBean> iterator()
    {
        HashMap<String, Object> current = new HashMap<String, Object>();
        current.put(ViewFieldEnum.SIZE_VIEW__SIZE.getName(), size);
        return new SingleEventIterator(statementContext.getEventAdapterService().adaptorForTypedMap(current, eventType));
    }

    public final String toString()
    {
        return this.getClass().getName();
    }

    /**
     * Creates the event type for this view
     * @param statementContext is the event adapter service
     * @return event type for view
     */
    protected static EventType createEventType(StatementContext statementContext)
    {
        Map<String, Object> schemaMap = new HashMap<String, Object>();
        schemaMap.put(ViewFieldEnum.SIZE_VIEW__SIZE.getName(), long.class);
        return statementContext.getEventAdapterService().createAnonymousMapType(schemaMap);
    }
}
