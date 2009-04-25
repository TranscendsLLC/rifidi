/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.collection.ArrayDequeJDK6Backport;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.metric.MetricReportingPath;
import com.espertech.esper.epl.metric.MetricReportingService;
import com.espertech.esper.epl.metric.StatementMetricHandle;
import com.espertech.esper.core.thread.ThreadingOption;
import com.espertech.esper.core.thread.ThreadingService;
import com.espertech.esper.core.thread.OutboundUnitRunnable;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.view.ViewSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements tracking of statement listeners and subscribers for a given statement
 * such as to efficiently dispatch in situations where 0, 1 or more listeners
 * are attached and/or 0 or 1 subscriber (such as iteration-only statement).
 */
public class StatementResultServiceImpl implements StatementResultService
{
    private static Log log = LogFactory.getLog(StatementResultServiceImpl.class);

    private final StatementLifecycleSvc statementLifecycleSvc;
    private final MetricReportingService metricReportingService;
    private final ThreadingService threadingService;

    // Part of the statement context
    private EPStatementSPI epStatement;
    private EPServiceProvider epServiceProvider;
    private boolean isInsertInto;
    private boolean isPattern;
    private StatementMetricHandle statementMetricHandle;

    // For natural delivery derived out of select-clause expressions
    private Class[] selectClauseTypes;
    private String[] selectClauseColumnNames;

    // Listeners and subscribers and derived information
    private EPStatementListenerSet statementListenerSet;
    private boolean isMakeNatural;
    private boolean isMakeSynthetic;
    private ResultDeliveryStrategy statementResultNaturalStrategy;

    // For iteration over patterns
    private EventBean lastIterableEvent;

    /**
     * Buffer for holding dispatchable events.
     */
    protected ThreadLocal<ArrayDequeJDK6Backport<UniformPair<EventBean[]>>> lastResults = new ThreadLocal<ArrayDequeJDK6Backport<UniformPair<EventBean[]>>>() {
        protected synchronized ArrayDequeJDK6Backport<UniformPair<EventBean[]>> initialValue() {
            return new ArrayDequeJDK6Backport<UniformPair<EventBean[]>>();
        }
    };

    /**
     * Ctor.
     * @param statementLifecycleSvc handles persistence for statements
     * @param metricReportingService for metrics reporting
     * @param threadingService for outbound threading
     */
    public StatementResultServiceImpl(StatementLifecycleSvc statementLifecycleSvc, MetricReportingService metricReportingService,
                                      ThreadingService threadingService)
    {
        log.debug(".ctor");
        this.statementLifecycleSvc = statementLifecycleSvc;
        this.metricReportingService = metricReportingService;
        this.threadingService = threadingService;
    }

    public void setContext(EPStatementSPI epStatement, EPServiceProvider epServiceProvider,
                           boolean isInsertInto, boolean isPattern, StatementMetricHandle statementMetricHandle)
    {
        this.epStatement = epStatement;
        this.epServiceProvider = epServiceProvider;
        this.isInsertInto = isInsertInto;
        this.isPattern = isPattern;
        isMakeSynthetic = isInsertInto || isPattern;
        this.statementMetricHandle = statementMetricHandle;
    }

    public void setSelectClause(Class[] selectClauseTypes, String[] selectClauseColumnNames)
    {
        if ((selectClauseTypes == null) || (selectClauseTypes.length == 0))
        {
            throw new IllegalArgumentException("Invalid null or zero-element list of select clause expression types");
        }
        if ((selectClauseColumnNames == null) || (selectClauseColumnNames.length == 0))
        {
            throw new IllegalArgumentException("Invalid null or zero-element list of select clause column names");
        }
        this.selectClauseTypes = selectClauseTypes;
        this.selectClauseColumnNames = selectClauseColumnNames;
    }

    public boolean isMakeSynthetic()
    {
        return isMakeSynthetic;
    }

    public boolean isMakeNatural()
    {
        return isMakeNatural;
    }

    public EventBean getLastIterableEvent()
    {
        return lastIterableEvent;
    }

    public void setUpdateListeners(EPStatementListenerSet statementListenerSet)
    {
        // indicate that listeners were updated for potential persistence of listener set, once the statement context is known
        if (epStatement != null)
        {
            this.statementLifecycleSvc.updatedListeners(epStatement.getStatementId(), epStatement.getName(), statementListenerSet);
        }

        this.statementListenerSet = statementListenerSet;

        isMakeNatural = statementListenerSet.getSubscriber() != null;
        isMakeSynthetic = !(statementListenerSet.getListeners().isEmpty() && statementListenerSet.getStmtAwareListeners().isEmpty())
                || isPattern || isInsertInto;

        if (statementListenerSet.getSubscriber() == null)
        {
            statementResultNaturalStrategy = null;
            isMakeNatural = false;
            return;
        }

        statementResultNaturalStrategy = ResultDeliveryStrategyFactory.create(statementListenerSet.getSubscriber(),
                selectClauseTypes, selectClauseColumnNames);
        isMakeNatural = true;
    }

    // Called by OutputProcessView
    public void indicate(UniformPair<EventBean[]> results)
    {
        if (results != null)
        {
            if ((MetricReportingPath.isMetricsEnabled) && (statementMetricHandle.isEnabled()))            
            {
                int numIStream = (results.getFirst() != null) ? results.getFirst().length : 0;
                int numRStream = (results.getSecond() != null) ? results.getSecond().length : 0;
                this.metricReportingService.accountOutput(statementMetricHandle, numIStream, numRStream);
            }

            if ((results.getFirst() != null) && (results.getFirst().length != 0))
            {
                lastResults.get().add(results);
                lastIterableEvent = results.getFirst()[0];
            }
            else if ((results.getSecond() != null) && (results.getSecond().length != 0))
            {
                lastResults.get().add(results);
            }
        }
    }

    public void execute()
    {
        ArrayDequeJDK6Backport<UniformPair<EventBean[]>> dispatches = lastResults.get();
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".execute dispatches: " + dispatches.size());
        }

        UniformPair<EventBean[]> events = EventBeanUtility.flattenList(dispatches);

        if (ExecutionPathDebugLog.isDebugEnabled && log.isDebugEnabled())
        {
            ViewSupport.dumpUpdateParams(".execute", events);
        }

        if ((ThreadingOption.isThreadingEnabled) && (threadingService.isOutboundThreading()))
        {
            threadingService.submitOutbound(new OutboundUnitRunnable(events, this));
        }
        else
        {
            processDispatch(events);
        }
        
        dispatches.clear();
    }

    /**
     * Indicate an outbound result.
     * @param events to indicate 
     */
    public void processDispatch(UniformPair<EventBean[]> events)
    {
        if (statementResultNaturalStrategy != null)
        {
            statementResultNaturalStrategy.execute(events);
        }

        EventBean[] newEventArr = events != null ? events.getFirst() : null;
        EventBean[] oldEventArr = events != null ? events.getSecond() : null;

        for (UpdateListener listener : statementListenerSet.listeners)
        {
            listener.update(newEventArr, oldEventArr);
        }
        if (!(statementListenerSet.stmtAwareListeners.isEmpty()))
        {
            for (StatementAwareUpdateListener listener : statementListenerSet.getStmtAwareListeners())
            {
                listener.update(newEventArr, oldEventArr, epStatement, epServiceProvider);
            }
        }
    }

    /**
     * Dispatches when the statement is stopped any remaining results.
     */
    public void dispatchOnStop()
    {
        lastIterableEvent = null;
        ArrayDequeJDK6Backport<UniformPair<EventBean[]>> dispatches = lastResults.get();
        if (dispatches.isEmpty())
        {
            return;
        }
        execute();
    }
}
