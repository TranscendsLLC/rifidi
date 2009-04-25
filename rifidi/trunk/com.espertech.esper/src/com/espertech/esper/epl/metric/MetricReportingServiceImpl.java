/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.metric;

import com.espertech.esper.client.ConfigurationMetricsReporting;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.metric.MetricEvent;
import com.espertech.esper.core.EPServicesContext;
import com.espertech.esper.core.StatementLifecycleObserver;
import com.espertech.esper.core.StatementLifecycleEvent;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.util.MetricUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Metrics reporting.
 * <p>
 * Reports for all statements even if not in a statement group, i.e. statement in default group.
 */
public class MetricReportingServiceImpl implements MetricReportingService, MetricEventRouter, StatementLifecycleObserver
{
    private static final Log log = LogFactory.getLog(MetricReportingServiceImpl.class);

    private final ConfigurationMetricsReporting specification;
    private final String engineUri;

    private volatile MetricExecutionContext executionContext;

    private boolean isScheduled;
    private final MetricScheduleService schedule;
    private final StatementMetricRepository stmtMetricRepository;

    private MetricExecEngine metricExecEngine;
    private MetricExecStatement metricExecStmtGroupDefault;
    private Map<String, MetricExecStatement> statementGroupExecutions;

    private final Map<String, StatementMetricHandle> statementMetricHandles;
    private final MetricsExecutor metricsExecutor;

    /**
     * Ctor.
     * @param specification configuration
     * @param engineUri engine URI
     */
    public MetricReportingServiceImpl(ConfigurationMetricsReporting specification, String engineUri)
    {
        if (specification.isEnableMetricsReporting())
        {
            MetricUtil.initialize();
        }
        this.specification = specification;
        this.engineUri = engineUri;
        schedule = new MetricScheduleService();

        stmtMetricRepository = new StatementMetricRepository(engineUri, specification);
        statementGroupExecutions = new LinkedHashMap<String, MetricExecStatement>();
        statementMetricHandles = new HashMap<String, StatementMetricHandle>(); 

        if (specification.isThreading())
        {
            metricsExecutor = new MetricsExecutorThreaded(engineUri);
        }
        else
        {
            metricsExecutor = new MetricsExecutorUnthreaded();
        }
    }

    public void setContext(EPRuntime runtime, EPServicesContext servicesContext)
    {
        MetricExecutionContext metricsExecutionContext = new MetricExecutionContext(servicesContext, runtime, stmtMetricRepository);

        // create all engine and statement executions
        metricExecEngine = new MetricExecEngine(this, engineUri, schedule, specification.getEngineInterval());
        metricExecStmtGroupDefault = new MetricExecStatement(this, schedule, specification.getStatementInterval(), 0);

        int countGroups = 1;
        for (Map.Entry<String, ConfigurationMetricsReporting.StmtGroupMetrics> entry : specification.getStatementGroups().entrySet())
        {
            ConfigurationMetricsReporting.StmtGroupMetrics config = entry.getValue();
            MetricExecStatement metricsExecution = new MetricExecStatement(this, schedule, config.getInterval(), countGroups);
            this.statementGroupExecutions.put(entry.getKey(), metricsExecution);
            countGroups++;
        }

        // last assign this volatile variable so the time event processing may schedule callbacks 
        executionContext = metricsExecutionContext;
    }

    public void processTimeEvent(long timeEventTime)
    {
        if (!MetricReportingPath.isMetricsEnabled)
        {
            return;
        }
        
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".processTimeEvent Setting time and evaluating schedules");
        }

        schedule.setTime(timeEventTime);
        if (!isScheduled)
        {
            if (executionContext != null)
            {
                scheduleExecutions();
                isScheduled = true;
            }
            else
            {
                return; // not initialized yet, race condition and must wait till initialized
            }
        }

        // fast evaluation against nearest scheduled time
        Long nearestTime = schedule.getNearestTime();
        if ((nearestTime == null) || (nearestTime > timeEventTime))
        {
            return;
        }

        // get executions
        List<MetricExec> executions = new ArrayList<MetricExec>(2);
        schedule.evaluate(executions);
        if (executions.isEmpty())
        {
            return;
        }

        // execute
        if (executionContext == null)
        {
            log.debug(".processTimeEvent No execution context");
            return;
        }
        
        for (MetricExec execution : executions)
        {
            metricsExecutor.execute(execution, executionContext);
        }
    }

    public void destroy()
    {
        schedule.clear();
        metricsExecutor.destroy();
    }

    public void route(MetricEvent metricEvent)
    {
        executionContext.getRuntime().sendEvent(metricEvent);
    }

    public void accountTime(StatementMetricHandle metricsHandle, long deltaCPU, long deltaWall)
    {
        stmtMetricRepository.accountTimes(metricsHandle, deltaCPU, deltaWall);
    }

    public void accountOutput(StatementMetricHandle handle, int numIStream, int numRStream)
    {
        stmtMetricRepository.accountOutput(handle, numIStream, numRStream);
    }

    public StatementMetricHandle getStatementHandle(String statementId, String statementName)
    {
        StatementMetricHandle handle = stmtMetricRepository.addStatement(statementName);
        statementMetricHandles.put(statementName, handle);
        return handle;
    }

    public void observe(StatementLifecycleEvent event)
    {
        if (!MetricReportingPath.isMetricsEnabled)
        {
            return;   
        }

        if (event.getEventType() == StatementLifecycleEvent.LifecycleEventType.STATECHANGE)
        {
            if (event.getStatement().isDestroyed())
            {
                stmtMetricRepository.removeStatement(event.getStatement().getName());
                statementMetricHandles.remove(event.getStatement().getName());
            }
        }
    }

    public void setMetricsReportingInterval(String stmtGroupName, long newInterval)
    {
        if (stmtGroupName == null)
        {
            metricExecStmtGroupDefault.setInterval(newInterval);
            return;
        }
        
        MetricExecStatement exec = this.statementGroupExecutions.get(stmtGroupName);
        if (exec == null)
        {
            throw new IllegalArgumentException("Statement group by name '" + stmtGroupName + "' could not be found");
        }
        exec.setInterval(newInterval);
    }

    private boolean isConsiderSchedule(long value)
    {
        if ((value > 0) && (value < Long.MAX_VALUE))
        {
            return true;
        }
        return false;
    }

    public void setMetricsReportingStmtDisabled(String statementName) throws ConfigurationException
    {
        StatementMetricHandle handle = statementMetricHandles.get(statementName);
        if (handle == null)
        {
            throw new ConfigurationException("Statement by name '" + statementName + "' not found in metrics collection");
        }
        handle.setEnabled(false);
    }

    public void setMetricsReportingStmtEnabled(String statementName) throws ConfigurationException
    {
        StatementMetricHandle handle = statementMetricHandles.get(statementName);
        if (handle == null)
        {
            throw new ConfigurationException("Statement by name '" + statementName + "' not found in metrics collection");
        }
        handle.setEnabled(true);
    }

    public void setMetricsReportingEnabled()
    {
        if (!specification.isEnableMetricsReporting())
        {
            throw new ConfigurationException("Metrics reporting must be enabled through initialization-time configuration");
        }
        scheduleExecutions();
        MetricReportingPath.setMetricsEnabled(true);
    }

    public void setMetricsReportingDisabled()
    {
        schedule.clear();
        MetricReportingPath.setMetricsEnabled(false);
    }

    private void scheduleExecutions()
    {
        if (!specification.isEnableMetricsReporting())
        {
            return;
        }

        if (isConsiderSchedule(metricExecEngine.getInterval()))
        {
            schedule.add(metricExecEngine.getInterval(), metricExecEngine);
        }

        // schedule each statement group, count the "default" group as the first group
        if (isConsiderSchedule(metricExecStmtGroupDefault.getInterval()))
        {
            schedule.add(metricExecStmtGroupDefault.getInterval(), metricExecStmtGroupDefault);
        }

        for (MetricExecStatement metricsExecution : statementGroupExecutions.values())
        {
            if (isConsiderSchedule(metricsExecution.getInterval()))
            {
                schedule.add(metricsExecution.getInterval(), metricsExecution);
            }
        }
    }
}
