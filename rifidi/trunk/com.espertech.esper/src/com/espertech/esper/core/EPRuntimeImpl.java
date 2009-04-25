/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.TimerControlEvent;
import com.espertech.esper.client.time.TimerEvent;
import com.espertech.esper.client.util.EventRenderer;
import com.espertech.esper.collection.ArrayBackedCollection;
import com.espertech.esper.collection.ArrayDequeJDK6Backport;
import com.espertech.esper.collection.ThreadWorkQueue;
import com.espertech.esper.epl.metric.MetricReportingPath;
import com.espertech.esper.epl.spec.SelectClauseStreamSelectorEnum;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.spec.StatementSpecRaw;
import com.espertech.esper.core.thread.*;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.event.util.EventRendererImpl;
import com.espertech.esper.filter.FilterHandle;
import com.espertech.esper.filter.FilterHandleCallback;
import com.espertech.esper.schedule.ScheduleHandle;
import com.espertech.esper.schedule.ScheduleHandleCallback;
import com.espertech.esper.timer.TimerCallback;
import com.espertech.esper.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements runtime interface. Also accepts timer callbacks for synchronizing time events with regular events
 * sent in.
 */
public class EPRuntimeImpl implements EPRuntimeSPI, EPRuntimeEventSender, TimerCallback, InternalEventRouter
{
    private EPServicesContext services;
    private boolean isLatchStatementInsertStream;
    private boolean isUsingExternalClocking;
    private boolean isSubselectPreeval;
    private volatile UnmatchedListener unmatchedListener;
    private AtomicLong routedInternal;
    private AtomicLong routedExternal;
    private EventRenderer eventRenderer;

    private ThreadLocal<ArrayBackedCollection<FilterHandle>> matchesArrayThreadLocal = new ThreadLocal<ArrayBackedCollection<FilterHandle>>()
    {
        protected synchronized ArrayBackedCollection<FilterHandle> initialValue()
        {
            return new ArrayBackedCollection<FilterHandle>(100);
        }
    };

    private ThreadLocal<HashMap<EPStatementHandle, ArrayDequeJDK6Backport<FilterHandleCallback>>> matchesPerStmtThreadLocal =
            new ThreadLocal<HashMap<EPStatementHandle, ArrayDequeJDK6Backport<FilterHandleCallback>>>()
    {
        protected synchronized HashMap<EPStatementHandle, ArrayDequeJDK6Backport<FilterHandleCallback>> initialValue()
        {
            return new HashMap<EPStatementHandle, ArrayDequeJDK6Backport<FilterHandleCallback>>(10000);
        }
    };

    private ThreadLocal<ArrayBackedCollection<ScheduleHandle>> scheduleArrayThreadLocal = new ThreadLocal<ArrayBackedCollection<ScheduleHandle>>()
    {
        protected synchronized ArrayBackedCollection<ScheduleHandle> initialValue()
        {
            return new ArrayBackedCollection<ScheduleHandle>(100);
        }
    };

    private ThreadLocal<HashMap<EPStatementHandle, Object>> schedulePerStmtThreadLocal =
            new ThreadLocal<HashMap<EPStatementHandle, Object>>()
    {
        protected synchronized HashMap<EPStatementHandle, Object> initialValue()
        {
            return new HashMap<EPStatementHandle, Object>(10000);
        }
    };

    /**
     * Constructor.
     * @param services - references to services
     */
    public EPRuntimeImpl(EPServicesContext services)
    {
        this.services = services;
        isLatchStatementInsertStream = this.services.getEngineSettingsService().getEngineSettings().getThreading().isInsertIntoDispatchPreserveOrder();
        isUsingExternalClocking = !this.services.getEngineSettingsService().getEngineSettings().getThreading().isInternalTimerEnabled();
        isSubselectPreeval = services.getEngineSettingsService().getEngineSettings().getExpression().isSelfSubselectPreeval();
        routedInternal = new AtomicLong();
        routedExternal = new AtomicLong();

        services.getThreadingService().initThreading(services, this);
    }

    public long getRoutedInternal()
    {
        return routedInternal.get();
    }

    public long getRoutedExternal()
    {
        return routedExternal.get();
    }

    public void timerCallback()
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled() && (ExecutionPathDebugLog.isTimerDebugEnabled)))
        {
            log.debug(".timerCallback Evaluating scheduled callbacks");
        }

        long msec = services.getTimeSource().getTimeMillis();
        CurrentTimeEvent currentTimeEvent = new CurrentTimeEvent(msec);
        sendEvent(currentTimeEvent);
    }

    public void sendEvent(Object event) throws EPException
    {
        if (event == null)
        {
            log.fatal(".sendEvent Null object supplied");
            return;
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            if ((!(event instanceof CurrentTimeEvent)) || (ExecutionPathDebugLog.isTimerDebugEnabled))
            {
                log.debug(".sendEvent Processing event " + event);
            }
        }

        // Process event
        if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isInboundThreading()))
        {
            services.getThreadingService().submitInbound(new InboundUnitSendEvent(event, this));
        }
        else
        {
            processEvent(event);
        }
    }

    public void sendEvent(org.w3c.dom.Node document) throws EPException
    {
        if (document == null)
        {
            log.fatal(".sendEvent Null object supplied");
            return;
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sendEvent Processing DOM node event " + document);
        }

        // Process event
        if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isInboundThreading()))
        {
            services.getThreadingService().submitInbound(new InboundUnitSendDOM(document, services, this));
        }
        else
        {
            // Get it wrapped up, process event
            EventBean eventBean = services.getEventAdapterService().adapterForDOM(document);
            processEvent(eventBean);
        }
    }

    public void route(org.w3c.dom.Node document) throws EPException
    {
        if (document == null)
        {
            log.fatal(".sendEvent Null object supplied");
            return;
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sendEvent Processing DOM node event " + document);
        }

        // Get it wrapped up, process event
        EventBean eventBean = services.getEventAdapterService().adapterForDOM(document);
        ThreadWorkQueue.add(eventBean);
    }

    public void sendEvent(Map map, String eventTypeName) throws EPException
    {
        if (map == null)
        {
            throw new IllegalArgumentException("Invalid null event object");
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sendMap Processing event " + map);
        }

        if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isInboundThreading()))
        {
            services.getThreadingService().submitInbound(new InboundUnitSendMap(map, eventTypeName, services, this));            
        }
        else
        {
            // Process event
            EventBean eventBean = services.getEventAdapterService().adapterForMap(map, eventTypeName);
            processWrappedEvent(eventBean);
        }
    }

    public void route(Map map, String eventTypeName) throws EPException
    {
        if (map == null)
        {
            throw new IllegalArgumentException("Invalid null event object");
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".route Processing event " + map);
        }

        // Process event
        EventBean event = services.getEventAdapterService().adapterForMap(map, eventTypeName);
        ThreadWorkQueue.add(event);
    }

    public long getNumEventsEvaluated()
    {
        return services.getFilterService().getNumEventsEvaluated();
    }

    public void resetStats() {
        services.getFilterService().resetStats();
        routedInternal.set(0);
        routedExternal.set(0);
    }

    public void routeEventBean(EventBean event)
    {
        ThreadWorkQueue.add(event);
    }

    public void route(Object event)
    {
        routedExternal.incrementAndGet();
        ThreadWorkQueue.add(event);
    }

    // Internal route of events via insert-into, holds a statement lock
    public void route(EventBean event, EPStatementHandle epStatementHandle)
    {
        routedInternal.incrementAndGet();

        if (isLatchStatementInsertStream)
        {
            InsertIntoLatchFactory insertIntoLatchFactory = epStatementHandle.getInsertIntoLatchFactory();
            Object latch = insertIntoLatchFactory.newLatch(event);
            ThreadWorkQueue.add(latch);
        }
        else
        {
            ThreadWorkQueue.add(event);
        }
    }

    /**
     * Process an unwrapped event.
     * @param event to process.
     */
    public void processEvent(Object event)
    {
        if (event instanceof TimerEvent)
        {
            processTimeEvent((TimerEvent) event);
            return;
        }

        EventBean eventBean;

        if (event instanceof EventBean)
        {
            eventBean = (EventBean) event;
        }
        else
        {
            eventBean = services.getEventAdapterService().adapterForBean(event);
        }

        processWrappedEvent(eventBean);
    }

    public void processWrappedEvent(EventBean eventBean)
    {
        // Acquire main processing lock which locks out statement management
        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            throw new EPException(ex);
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }

        // Dispatch results to listeners
        // Done outside of the read-lock to prevent lockups when listeners create statements
        dispatch();

        // Work off the event queue if any events accumulated in there via a route() or insert-into
        processThreadWorkQueue();
    }

    private void processTimeEvent(TimerEvent event)
    {
        if (event instanceof TimerControlEvent)
        {
            TimerControlEvent timerControlEvent = (TimerControlEvent) event;
            if (timerControlEvent.getClockType() == TimerControlEvent.ClockType.CLOCK_INTERNAL)
            {
                // Start internal clock which supplies CurrentTimeEvent events every 100ms
                // This may be done without delay thus the write lock indeed must be reentrant.
                services.getTimerService().startInternalClock();
                isUsingExternalClocking = false;
            }
            else
            {
                // Stop internal clock, for unit testing and for external clocking
                services.getTimerService().stopInternalClock(true);
                isUsingExternalClocking = true;
            }

            return;
        }

        // Evaluation of all time events is protected from statement management
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()) && (ExecutionPathDebugLog.isTimerDebugEnabled))
        {
            log.debug(".processTimeEvent Setting time and evaluating schedules");
        }

        CurrentTimeEvent current = (CurrentTimeEvent) event;
        long currentTime = current.getTimeInMillis();

        if (isUsingExternalClocking && (currentTime == services.getSchedulingService().getTime()))
        {
            if (log.isWarnEnabled())
            {
                log.warn("Duplicate time event received for currentTime " + currentTime);
            }
        }
        services.getSchedulingService().setTime(currentTime);

        if (MetricReportingPath.isMetricsEnabled)
        {
            services.getMetricsReportingService().processTimeEvent(currentTime);
        }

        processSchedule();

        // Let listeners know of results
        dispatch();

        // Work off the event queue if any events accumulated in there via a route()
        processThreadWorkQueue();
    }

    private void processSchedule()
    {
        ArrayBackedCollection<ScheduleHandle> handles = scheduleArrayThreadLocal.get();

        // Evaluation of schedules is protected by an optional scheduling service lock and then the engine lock
        // We want to stay in this order for allowing the engine lock as a second-order lock to the
        // services own lock, if it has one.
        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            services.getSchedulingService().evaluate(handles);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processScheduleHandles(handles);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }
    }

    private void processScheduleHandles(ArrayBackedCollection<ScheduleHandle> handles)
    {
        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.trace("Found schedules for", handles.size());
        }

        if (handles.size() == 0)
        {
            return;
        }

        // handle 1 result separatly for performance reasons
        if (handles.size() == 1)
        {
            Object[] handleArray = handles.getArray();
            EPStatementHandleCallback handle = (EPStatementHandleCallback) handleArray[0];

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getEpStatementHandle().getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementScheduleSingle(handle, services);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                services.getMetricsReportingService().accountTime(handle.getEpStatementHandle().getMetricsHandle(), deltaCPU, deltaWall);
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isTimerThreading()))
                {
                    services.getThreadingService().submitTimerWork(new TimerUnitSingle(services, this, handle));
                }
                else
                {
                    processStatementScheduleSingle(handle, services);
                }
            }

            handles.clear();
            return;
        }

        Object[] matchArray = handles.getArray();
        int entryCount = handles.size();

        // sort multiple matches for the event into statements
        HashMap<EPStatementHandle, Object> stmtCallbacks = schedulePerStmtThreadLocal.get();
        stmtCallbacks.clear();
        for (int i = 0; i < entryCount; i++)    // need to use the size of the collection
        {
            EPStatementHandleCallback handleCallback = (EPStatementHandleCallback) matchArray[i];
            EPStatementHandle handle = handleCallback.getEpStatementHandle();
            ScheduleHandleCallback callback = handleCallback.getScheduleCallback();

            Object entry = stmtCallbacks.get(handle);

            // This statement has not been encountered before
            if (entry == null)
            {
                stmtCallbacks.put(handle, callback);
                continue;
            }

            // This statement has been encountered once before
            if (entry instanceof ScheduleHandleCallback)
            {
                ScheduleHandleCallback existingCallback = (ScheduleHandleCallback) entry;
                ArrayDequeJDK6Backport<ScheduleHandleCallback> entries = new ArrayDequeJDK6Backport<ScheduleHandleCallback>();
                entries.add(existingCallback);
                entries.add(callback);
                stmtCallbacks.put(handle, entries);
                continue;
            }

            // This statement has been encountered more then once before
            ArrayDequeJDK6Backport<ScheduleHandleCallback> entries = (ArrayDequeJDK6Backport<ScheduleHandleCallback>) entry;
            entries.add(callback);
        }
        handles.clear();

        for (Map.Entry<EPStatementHandle, Object> entry : stmtCallbacks.entrySet())
        {
            EPStatementHandle handle = entry.getKey();
            Object callbackObject = entry.getValue();

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementScheduleMultiple(handle, callbackObject, services);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                services.getMetricsReportingService().accountTime(handle.getMetricsHandle(), deltaCPU, deltaWall);
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isTimerThreading()))
                {
                    services.getThreadingService().submitTimerWork(new TimerUnitMultiple(services, this, handle, callbackObject));
                }
                else
                {
                    processStatementScheduleMultiple(handle, callbackObject, services);
                }
            }
        }
    }

    /**
     * Works off the thread's work queue.
     */
    public void processThreadWorkQueue()
    {
        Object item;
        while ( (item = ThreadWorkQueue.next()) != null)
        {
            if (item instanceof InsertIntoLatchSpin)
            {
                processThreadWorkQueueLatchedSpin((InsertIntoLatchSpin) item);
            }
            else if (item instanceof InsertIntoLatchWait)
            {
                processThreadWorkQueueLatchedWait((InsertIntoLatchWait) item);
            }
            else
            {
                processThreadWorkQueueUnlatched(item);
            }
        }

        // Process named window deltas
        boolean haveDispatched = services.getNamedWindowService().dispatch();
        if (haveDispatched)
        {
            // Dispatch results to listeners
            dispatch();
        }

        if (!(ThreadWorkQueue.isEmpty()))
        {
            processThreadWorkQueue();
        }
    }

    private void processThreadWorkQueueLatchedWait(InsertIntoLatchWait insertIntoLatch)
    {
        // wait for the latch to complete
        Object item = insertIntoLatch.await();

        EventBean eventBean;
        if (item instanceof EventBean)
        {
            eventBean = (EventBean) item;
        }
        else
        {
            eventBean = services.getEventAdapterService().adapterForBean(item);
        }

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            insertIntoLatch.done();
            services.getEventProcessingRWLock().releaseReadLock();
        }

        dispatch();
    }

    private void processThreadWorkQueueLatchedSpin(InsertIntoLatchSpin insertIntoLatch)
    {
        // wait for the latch to complete
        Object item = insertIntoLatch.await();

        EventBean eventBean;
        if (item instanceof EventBean)
        {
            eventBean = (EventBean) item;
        }
        else
        {
            eventBean = services.getEventAdapterService().adapterForBean(item);
        }

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            insertIntoLatch.done();
            services.getEventProcessingRWLock().releaseReadLock();
        }

        dispatch();
    }

    private void processThreadWorkQueueUnlatched(Object item)
    {
        EventBean eventBean;
        if (item instanceof EventBean)
        {
            eventBean = (EventBean) item;
        }
        else
        {
            eventBean = services.getEventAdapterService().adapterForBean(item);
        }

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }

        dispatch();
    }

    private void processMatches(EventBean event)
    {
        // get matching filters
        ArrayBackedCollection<FilterHandle> matches = matchesArrayThreadLocal.get();
        services.getFilterService().evaluate(event, matches);

        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.trace("Found matches for underlying ", matches.size(), event.getUnderlying());
        }

        if (matches.size() == 0)
        {
            if (unmatchedListener != null)
            {
                unmatchedListener.update(event);
            }
            return;
        }

        HashMap<EPStatementHandle, ArrayDequeJDK6Backport<FilterHandleCallback>> stmtCallbacks = matchesPerStmtThreadLocal.get();
        Object[] matchArray = matches.getArray();
        int entryCount = matches.size();

        for (int i = 0; i < entryCount; i++)
        {
            EPStatementHandleCallback handleCallback = (EPStatementHandleCallback) matchArray[i];
            EPStatementHandle handle = handleCallback.getEpStatementHandle();

            // Self-joins require that the internal dispatch happens after all streams are evaluated
            if (handle.isCanSelfJoin())
            {
                ArrayDequeJDK6Backport<FilterHandleCallback> callbacks = stmtCallbacks.get(handle);
                if (callbacks == null)
                {
                    callbacks = new ArrayDequeJDK6Backport<FilterHandleCallback>();
                    stmtCallbacks.put(handle, callbacks);
                }
                callbacks.add(handleCallback.getFilterCallback());
                continue;
            }

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementFilterSingle(handle, handleCallback, event);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                services.getMetricsReportingService().accountTime(handle.getMetricsHandle(), deltaCPU, deltaWall);
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isRouteThreading()))
                {
                    services.getThreadingService().submitRoute(new RouteUnitSingle(this, handleCallback, event));
                }
                else
                {
                    processStatementFilterSingle(handle, handleCallback, event);
                }
            }
        }
        matches.clear();
        if (stmtCallbacks.isEmpty())
        {
            return;
        }

        for (Map.Entry<EPStatementHandle, ArrayDequeJDK6Backport<FilterHandleCallback>> entry : stmtCallbacks.entrySet())
        {
            EPStatementHandle handle = entry.getKey();
            ArrayDequeJDK6Backport<FilterHandleCallback> callbackList = entry.getValue();

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementFilterMultiple(handle, callbackList, event);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                services.getMetricsReportingService().accountTime(handle.getMetricsHandle(), deltaCPU, deltaWall);
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isRouteThreading()))
                {
                    services.getThreadingService().submitRoute(new RouteUnitMultiple(this, callbackList, event, handle));
                }
                else
                {
                    processStatementFilterMultiple(handle, callbackList, event);
                }
            }
        }
        stmtCallbacks.clear();
    }

    /**
     * Processing multiple schedule matches for a statement.
     * @param handle statement handle
     * @param callbackObject object containing matches
     * @param services engine services
     */
    public static void processStatementScheduleMultiple(EPStatementHandle handle, Object callbackObject, EPServicesContext services)
    {
        handle.getStatementLock().acquireLock(services.getStatementLockFactory());
        try
        {
            if (handle.isHasVariables())
            {
                services.getVariableService().setLocalVersion();
            }

            if (callbackObject instanceof ArrayDequeJDK6Backport)
            {
                ArrayDequeJDK6Backport<ScheduleHandleCallback> callbackList = (ArrayDequeJDK6Backport<ScheduleHandleCallback>) callbackObject;
                for (ScheduleHandleCallback callback : callbackList)
                {
                    callback.scheduledTrigger(services.getExtensionServicesContext());
                }
            }
            else
            {
                ScheduleHandleCallback callback = (ScheduleHandleCallback) callbackObject;
                callback.scheduledTrigger(services.getExtensionServicesContext());
            }

            // internal join processing, if applicable
            handle.internalDispatch();
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            handle.getStatementLock().releaseLock(services.getStatementLockFactory());
        }
    }

    /**
     * Processing single schedule matche for a statement.
     * @param handle statement handle
     * @param services engine services
     */
    public static void processStatementScheduleSingle(EPStatementHandleCallback handle, EPServicesContext services)
    {
        ManagedLock statementLock = handle.getEpStatementHandle().getStatementLock();
        statementLock.acquireLock(services.getStatementLockFactory());
        try
        {
            if (handle.getEpStatementHandle().isHasVariables())
            {
                services.getVariableService().setLocalVersion();
            }

            handle.getScheduleCallback().scheduledTrigger(services.getExtensionServicesContext());

            handle.getEpStatementHandle().internalDispatch();
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            handle.getEpStatementHandle().getStatementLock().releaseLock(services.getStatementLockFactory());
        }
    }

    /**
     * Processing multiple filter matches for a statement.
     * @param handle statement handle
     * @param callbackList object containing callbacks
     * @param event to process
     */
    public void processStatementFilterMultiple(EPStatementHandle handle, ArrayDequeJDK6Backport<FilterHandleCallback> callbackList, EventBean event)
    {
        handle.getStatementLock().acquireLock(services.getStatementLockFactory());
        try
        {
            if (handle.isHasVariables())
            {
                services.getVariableService().setLocalVersion();
            }

            if (isSubselectPreeval)
            {
                // sub-selects always go first
                for (FilterHandleCallback callback : callbackList)
                {
                    if (callback.isSubSelect())
                    {
                        callback.matchFound(event);
                    }
                }

                for (FilterHandleCallback callback : callbackList)
                {
                    if (!callback.isSubSelect())
                    {
                        callback.matchFound(event);
                    }
                }
            }
            else
            {
                // sub-selects always go last
                for (FilterHandleCallback callback : callbackList)
                {
                    if (!callback.isSubSelect())
                    {
                        callback.matchFound(event);
                    }
                }

                for (FilterHandleCallback callback : callbackList)
                {
                    if (callback.isSubSelect())
                    {
                        callback.matchFound(event);
                    }
                }
            }

            // internal join processing, if applicable
            handle.internalDispatch();
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            handle.getStatementLock().releaseLock(services.getStatementLockFactory());
        }
    }

    /**
     * Process a single match.
     * @param handle statement
     * @param handleCallback callback
     * @param event event to indicate
     */
    public void processStatementFilterSingle(EPStatementHandle handle, EPStatementHandleCallback handleCallback, EventBean event)
    {
        handle.getStatementLock().acquireLock(services.getStatementLockFactory());
        try
        {
            if (handle.isHasVariables())
            {
                services.getVariableService().setLocalVersion();
            }

            handleCallback.getFilterCallback().matchFound(event);

            // internal join processing, if applicable
            handle.internalDispatch();
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            handleCallback.getEpStatementHandle().getStatementLock().releaseLock(services.getStatementLockFactory());
        }
    }

    /**
     * Dispatch events.
     */
    public void dispatch()
    {
        try
        {
            services.getDispatchService().dispatch();
        }
        catch (RuntimeException ex)
        {
            throw new EPException(ex);
        }
    }

    /**
     * Destroy for destroying an engine instance: sets references to null and clears thread-locals
     */
    public void destroy()
    {
        services = null;

        matchesArrayThreadLocal.remove();
        matchesPerStmtThreadLocal.remove();
        scheduleArrayThreadLocal.remove();
        schedulePerStmtThreadLocal.remove();

        matchesArrayThreadLocal = null;
        matchesPerStmtThreadLocal = null;
        scheduleArrayThreadLocal = null;
        schedulePerStmtThreadLocal = null;
    }

    public void setUnmatchedListener(UnmatchedListener listener)
    {
        this.unmatchedListener = listener;
    }

    public void setVariableValue(String variableName, Object variableValue) throws EPException
    {
        VariableReader reader = services.getVariableService().getReader(variableName);
        if (reader == null)
        {
            throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
        }

        services.getVariableService().checkAndWrite(reader.getVariableNumber(), variableValue);
        services.getVariableService().commit();
    }

    public void setVariableValue(Map<String, Object> variableValues) throws EPException
    {
        for (Map.Entry<String, Object> entry : variableValues.entrySet())
        {
            String variableName = entry.getKey();
            VariableReader reader = services.getVariableService().getReader(variableName);
            if (reader == null)
            {
                services.getVariableService().rollback();
                throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
            }

            try
            {
                services.getVariableService().checkAndWrite(reader.getVariableNumber(), entry.getValue());
            }
            catch (RuntimeException ex)
            {
                services.getVariableService().rollback();
                throw ex;
            }
        }

        services.getVariableService().commit();
    }

    public Object getVariableValue(String variableName) throws EPException
    {
        services.getVariableService().setLocalVersion();
        VariableReader reader = services.getVariableService().getReader(variableName);
        if (reader == null)
        {
            throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
        }
        return reader.getValue();
    }

    public Map<String, Object> getVariableValue(Set<String> variableNames) throws EPException
    {
        services.getVariableService().setLocalVersion();
        Map<String, Object> values = new HashMap<String, Object>();
        for (String variableName : variableNames)
        {
            VariableReader reader = services.getVariableService().getReader(variableName);
            if (reader == null)
            {
                throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
            }

            Object value = reader.getValue();
            values.put(variableName, value);
        }
        return values;
    }

    public Map<String, Object> getVariableValueAll() throws EPException
    {
        services.getVariableService().setLocalVersion();
        Map<String, VariableReader> variables = services.getVariableService().getVariables();
        Map<String, Object> values = new HashMap<String, Object>();
        for (Map.Entry<String, VariableReader> entry : variables.entrySet())
        {
            Object value = entry.getValue().getValue();
            values.put(entry.getValue().getVariableName(), value);
        }
        return values;
    }

    public Map<String, Class> getVariableTypeAll()
    {
        Map<String, VariableReader> variables = services.getVariableService().getVariables();
        Map<String, Class> values = new HashMap<String, Class>();
        for (Map.Entry<String, VariableReader> entry : variables.entrySet())
        {
            Class type = entry.getValue().getType();
            values.put(entry.getValue().getVariableName(), type);
        }
        return values;
    }

    public Class getVariableType(String variableName)
    {
        VariableReader reader = services.getVariableService().getReader(variableName);
        if (reader == null)
        {
            return null;
        }
        return reader.getType();
    }

    public EPOnDemandQueryResult executeQuery(String epl)
    {
        try
        {
            EPPreparedExecuteMethod executeMethod = getExecuteMethod(epl);
            EPPreparedQueryResult result = executeMethod.execute();
            return new EPQueryResultImpl(result);
        }
        catch (EPStatementException ex)
        {
            throw ex;
        }
        catch (Throwable t)
        {
            String message = "Error executing statement: " + t.getMessage();
            log.debug(message, t);
            throw new EPStatementException(message, epl);
        }
    }


    public EPOnDemandPreparedQuery prepareQuery(String epl)
    {
        try
        {
            EPPreparedExecuteMethod startMethod = getExecuteMethod(epl);
            return new EPPreparedQueryImpl(startMethod, epl);
        }
        catch (EPStatementException ex)
        {
            throw ex;
        }
        catch (Throwable t)
        {
            String message = "Error executing statement: " + t.getMessage();
            log.debug(message, t);
            throw new EPStatementException(message, epl);
        }
    }

    private EPPreparedExecuteMethod getExecuteMethod(String epl)
    {
        String stmtName = UuidGenerator.generate();
        String stmtId = UuidGenerator.generate();

        try
        {
            StatementSpecRaw spec = EPAdministratorImpl.compileEPL(epl, stmtName, services, SelectClauseStreamSelectorEnum.ISTREAM_ONLY);
            StatementContext statementContext =  services.getStatementContextFactory().makeContext(stmtId, stmtName, epl, false, services, null, null, null, true);
            StatementSpecCompiled compiledSpec = StatementLifecycleSvcImpl.compile(spec, epl, statementContext, true);
            return new EPPreparedExecuteMethod(compiledSpec, services, statementContext);
        }
        catch (EPStatementException ex)
        {
            throw ex;
        }
        catch (Throwable t)
        {
            String message = "Error executing statement: " + t.getMessage();
            log.debug(message, t);
            throw new EPStatementException(message, epl);
        }
    }

    public EventSender getEventSender(String eventTypeName)
    {
        return services.getEventAdapterService().getStaticTypeEventSender(this, eventTypeName, services.getThreadingService());
    }

    public EventSender getEventSender(URI uri[]) throws EventTypeException
    {
        return services.getEventAdapterService().getDynamicTypeEventSender(this, uri, services.getThreadingService());
    }

    public EventRenderer getEventRenderer()
    {
        if (eventRenderer == null)
        {
            eventRenderer = new EventRendererImpl();
        }
        return eventRenderer;
    }

    public long getCurrentTime()
    {
        return services.getSchedulingService().getTime();
    }

    private static final Log log = LogFactory.getLog(EPRuntimeImpl.class);
}
