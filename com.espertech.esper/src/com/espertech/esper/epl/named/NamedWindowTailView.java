/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.collection.ArrayDequeJDK6Backport;
import com.espertech.esper.collection.ArrayEventIterator;
import com.espertech.esper.collection.NullIterator;
import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.core.StatementResultService;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.event.vaevent.ValueAddEventProcessor;
import com.espertech.esper.view.StatementStopService;
import com.espertech.esper.view.ViewSupport;
import com.espertech.esper.view.BatchingDataWindowView;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This view is hooked into a named window's view chain as the last view and handles dispatching of named window
 * insert and remove stream results via {@link NamedWindowService} to consuming statements.
 */
public class NamedWindowTailView extends ViewSupport implements Iterable<EventBean>
{
    private final static Iterator<EventBean> nullIterator = new NullIterator<EventBean>();
    private final EventType eventType;
    private final NamedWindowRootView namedWindowRootView;
    private final NamedWindowService namedWindowService;
    private volatile Map<EPStatementHandle, List<NamedWindowConsumerView>> consumers;  // handles as copy-on-write
    private final EPStatementHandle createWindowStmtHandle;
    private final StatementResultService statementResultService;
    private final ValueAddEventProcessor revisionProcessor;
    private volatile long numberOfEvents;

    /**
     * Ctor.
     * @param eventType the event type of the named window
     * @param namedWindowService the service for dispatches to consumers for hooking into the dispatch loop
     * @param namedWindowRootView the root data window view for indicating remove stream events to be removed from possible on-delete indexes
     * @param createWindowStmtHandle statement handle for the statement that created the named window, for safe iteration
     * @param statementResultService for coordinating on whether insert and remove stream events should be posted
     * @param revisionProcessor handles update events
     */
    public NamedWindowTailView(EventType eventType, NamedWindowService namedWindowService, NamedWindowRootView namedWindowRootView, EPStatementHandle createWindowStmtHandle, StatementResultService statementResultService, ValueAddEventProcessor revisionProcessor)
    {
        this.eventType = eventType;
        this.namedWindowService = namedWindowService;
        consumers = new HashMap<EPStatementHandle, List<NamedWindowConsumerView>>();
        this.namedWindowRootView = namedWindowRootView;
        this.createWindowStmtHandle = createWindowStmtHandle;
        this.statementResultService = statementResultService;
        this.revisionProcessor = revisionProcessor;
    }

    /**
     * Returns true to indicate that the data window view is a batch view.
     * @return true if batch view
     */
    public boolean isParentBatchWindow()
    {
        if (this.getParent() instanceof BatchingDataWindowView)
        {
            return true;
        }
        return false;
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        // Only old data (remove stream) needs to be removed from indexes (kept by root view), if any
        if (oldData != null)
        {
            namedWindowRootView.removeOldData(oldData);
            numberOfEvents -= oldData.length;
        }

        if (newData != null)
        {
            numberOfEvents += newData.length;
        }

        // Post to child views, only if there are listeners or subscribers
        if (statementResultService.isMakeNatural() || statementResultService.isMakeSynthetic())
        {
            updateChildren(newData, oldData);
        }

        // Add to dispatch list for later result dispatch by runtime
        NamedWindowDeltaData delta = new NamedWindowDeltaData(newData, oldData);
        namedWindowService.addDispatch(delta, consumers);
    }

    /**
     * Adds a consumer view keeping the consuming statement's handle and lock to coordinate dispatches.
     * @param statementHandle the statement handle
     * @param statementStopService for when the consumer stops, to unregister the consumer
     * @param filterList is a list of filter expressions
     * @return consumer representative view
     */
    public NamedWindowConsumerView addConsumer(List<ExprNode> filterList, EPStatementHandle statementHandle, StatementStopService statementStopService)
    {
        // Construct consumer view, allow a callback to this view to remove the consumer
        NamedWindowConsumerView consumerView = new NamedWindowConsumerView(filterList, eventType, statementStopService, this);

        // Keep a list of consumer views per statement to accomodate joins and subqueries
        List<NamedWindowConsumerView> viewsPerStatements = consumers.get(statementHandle);
        if (viewsPerStatements == null)
        {
            viewsPerStatements = new CopyOnWriteArrayList<NamedWindowConsumerView>();

            // avoid concurrent modification as a thread may currently iterate over consumers as its dispatching
            // without the engine lock
            Map<EPStatementHandle, List<NamedWindowConsumerView>> newConsumers = new HashMap<EPStatementHandle, List<NamedWindowConsumerView>>();
            newConsumers.putAll(consumers);
            newConsumers.put(statementHandle, viewsPerStatements);
            consumers = newConsumers;
        }
        viewsPerStatements.add(consumerView);

        return consumerView;
    }

    /**
     * Called by the consumer view to indicate it was stopped or destroyed, such that the
     * consumer can be deregistered and further dispatches disregard this consumer.
     * @param namedWindowConsumerView is the consumer representative view
     */
    public void removeConsumer(NamedWindowConsumerView namedWindowConsumerView)
    {
        EPStatementHandle handleRemoved = null;
        // Find the consumer view
        for (Map.Entry<EPStatementHandle, List<NamedWindowConsumerView>> entry : consumers.entrySet())
        {
            boolean foundAndRemoved = entry.getValue().remove(namedWindowConsumerView);
            // Remove the consumer view
            if ((foundAndRemoved) && (entry.getValue().size() == 0))
            {
                // Remove the handle if this list is now empty
                handleRemoved = entry.getKey();
                break;
            }
        }
        if (handleRemoved != null)
        {
            Map<EPStatementHandle, List<NamedWindowConsumerView>> newConsumers = new HashMap<EPStatementHandle, List<NamedWindowConsumerView>>();
            newConsumers.putAll(consumers);
            newConsumers.remove(handleRemoved);
            consumers = newConsumers;
        }
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public Iterator<EventBean> iterator()
    {
        if (revisionProcessor != null)
        {
            Collection<EventBean> coll = revisionProcessor.getSnapshot(createWindowStmtHandle, parent);
            return coll.iterator();
        }

        createWindowStmtHandle.getStatementLock().acquireLock(null);
        try
        {
            Iterator<EventBean> it = parent.iterator();
            if (!it.hasNext())
            {
                return nullIterator;
            }
            ArrayList<EventBean> list = new ArrayList<EventBean>();
            while (it.hasNext())
            {
                list.add(it.next());
            }
            return new ArrayEventIterator(list.toArray(new EventBean[list.size()]));
        }
        finally
        {
            createWindowStmtHandle.getStatementLock().releaseLock(null);
        }
    }

    /**
     * Returns a snapshot of window contents, thread-safely
     * @return window contents
     */
    public Collection<EventBean> snapshot()
    {
        if (revisionProcessor != null)
        {
            return revisionProcessor.getSnapshot(createWindowStmtHandle, parent);
        }

        createWindowStmtHandle.getStatementLock().acquireLock(null);
        try
        {
            Iterator<EventBean> it = parent.iterator();
            if (!it.hasNext())
            {
                return Collections.EMPTY_LIST;
            }
            ArrayDequeJDK6Backport<EventBean> list = new ArrayDequeJDK6Backport<EventBean>();
            while (it.hasNext())
            {
                list.add(it.next());
            }
            return list;
        }
        finally
        {
            createWindowStmtHandle.getStatementLock().releaseLock(null);
        }
    }

    /**
     * Destroy the view.
     */
    public void destroy()
    {
        consumers.clear();
    }

    /**
     * Returns the number of events held.
     * @return number of events
     */
    public long getNumberOfEvents()
    {
        return numberOfEvents;
    }
}
