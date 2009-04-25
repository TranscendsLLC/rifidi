/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.collection.ArrayEventIterator;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.core.InternalEventRouter;
import com.espertech.esper.core.StatementResultService;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.view.StatementStopService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * View for the on-select statement that handles selecting events from a named window.
 */
public class NamedWindowOnSelectView extends NamedWindowOnExprBaseView
{
    private static final Log log = LogFactory.getLog(NamedWindowOnSelectView.class);

    private final InternalEventRouter internalEventRouter;
    private final ResultSetProcessor resultSetProcessor;
    private final EPStatementHandle statementHandle;
    private final StatementResultService statementResultService;
    private EventBean[] lastResult;
    private Set<MultiKey<EventBean>> oldEvents = new HashSet<MultiKey<EventBean>>();

    /**
     * Ctor.
     * @param statementStopService for indicating a statement was stopped or destroyed for cleanup
     * @param lookupStrategy for handling trigger events to determine deleted events
     * @param rootView the named window root view
     * @param internalEventRouter for insert-into behavior
     * @param resultSetProcessor for processing aggregation, having and ordering
     * @param statementHandle required for routing events
     * @param statementResultService for coordinating on whether insert and remove stream events should be posted
     */
    public NamedWindowOnSelectView(StatementStopService statementStopService,
                                   LookupStrategy lookupStrategy,
                                   NamedWindowRootView rootView,
                                   InternalEventRouter internalEventRouter,
                                   ResultSetProcessor resultSetProcessor,
                                   EPStatementHandle statementHandle,
                                   StatementResultService statementResultService)
    {
        super(statementStopService, lookupStrategy, rootView);
        this.internalEventRouter = internalEventRouter;
        this.resultSetProcessor = resultSetProcessor;
        this.statementHandle = statementHandle;
        this.statementResultService = statementResultService;
    }

    public void handleMatching(EventBean[] triggerEvents, EventBean[] matchingEvents)
    {
        EventBean[] newData = null;

        // clear state from prior results
        resultSetProcessor.clear();

        // build join result
        Set<MultiKey<EventBean>> newEvents = new HashSet<MultiKey<EventBean>>();
        for (int i = 0; i < triggerEvents.length; i++)
        {
            EventBean triggerEvent = triggerEvents[0];
            if (matchingEvents != null)
            {
                for (int j = 0; j < matchingEvents.length; j++)
                {
                    EventBean[] eventsPerStream = new EventBean[2];
                    eventsPerStream[0] = matchingEvents[j];
                    eventsPerStream[1] = triggerEvent;
                    newEvents.add(new MultiKey<EventBean>(eventsPerStream));
                }
            }
        }

        // process matches
        UniformPair<EventBean[]> pair = resultSetProcessor.processJoinResult(newEvents, oldEvents, false);
        newData = pair.getFirst();

        if (internalEventRouter != null)
        {
            if (newData != null)
            {
                for (int i = 0; i < newData.length; i++)
                {
                    internalEventRouter.route(newData[i], statementHandle);
                }
            }
        }

        // The on-select listeners receive the events selected
        if ((newData != null) && (newData.length > 0))
        {
            // And post only if we have listeners/subscribers that need the data
            if (statementResultService.isMakeNatural() || statementResultService.isMakeSynthetic())
            {
                updateChildren(newData, null);
            }
        }
        lastResult = newData;
    }

    public EventType getEventType()
    {
        if (resultSetProcessor != null)
        {
            return resultSetProcessor.getResultEventType();
        }
        else
        {
            return namedWindowEventType;
        }
    }

    public Iterator<EventBean> iterator()
    {
        return new ArrayEventIterator(lastResult);
    }
}
