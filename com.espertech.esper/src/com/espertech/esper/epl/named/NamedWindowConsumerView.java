/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.collection.OneEventCollection;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.view.StatementStopCallback;
import com.espertech.esper.view.StatementStopService;
import com.espertech.esper.view.ViewSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a consumer of a named window that selects from a named window via a from-clause.
 * <p>
 * The view simply dispatches directly to child views, and keeps the last new event for iteration.
 */
public class NamedWindowConsumerView extends ViewSupport implements StatementStopCallback
{
    private static final Log log = LogFactory.getLog(NamedWindowConsumerView.class);
    private final List<ExprNode> filterList;
    private final EventType eventType;
    private final NamedWindowTailView tailView;
    private EventBean[] eventPerStream = new EventBean[1];

    /**
     * Ctor.
     * @param eventType the event type of the named window
     * @param statementStopService for registering a callback when the view stopped, to unregister the statement as a consumer
     * @param tailView to indicate when the consumer stopped to remove the consumer
     * @param filterList is a list of filter expressions
     */
    public NamedWindowConsumerView(List<ExprNode> filterList,
                                   EventType eventType,
                                   StatementStopService statementStopService,
                                   NamedWindowTailView tailView)
    {
        this.filterList = filterList;
        this.eventType = eventType;
        this.tailView = tailView;
        statementStopService.addSubscriber(this);
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".update Received update, " +
                    "  newData.length==" + ((newData == null) ? 0 : newData.length) +
                    "  oldData.length==" + ((oldData == null) ? 0 : oldData.length));
        }

        // if we have a filter for the named window,
        if (!filterList.isEmpty())
        {
            newData = passFilter(newData, true);
            oldData = passFilter(oldData, false);
        }

        if ((newData != null) || (oldData != null))
        {
            updateChildren(newData, oldData);
        }
    }

    private EventBean[] passFilter(EventBean[] eventData, boolean isNewData)
    {
        if ((eventData == null) || (eventData.length == 0))
        {
            return null;
        }

        OneEventCollection filtered = null;
        for (EventBean event : eventData)
        {
            eventPerStream[0] = event;
            boolean pass = true;
            for (ExprNode filter : filterList)
            {
                Boolean result = (Boolean) filter.evaluate(eventPerStream, isNewData);
                if ((result != null) && (!result))
                {
                    pass = false;
                    break;
                }
            }

            if (pass)
            {
                if (filtered == null)
                {
                    filtered = new OneEventCollection();
                }
                filtered.add(event);
            }
        }

        if (filtered == null)
        {
            return null;
        }
        return filtered.toArray();
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public Iterator<EventBean> iterator()
    {
        return new FilteredEventIterator(filterList, tailView.iterator());
    }

    public void statementStopped()
    {
        tailView.removeConsumer(this);
    }
}
