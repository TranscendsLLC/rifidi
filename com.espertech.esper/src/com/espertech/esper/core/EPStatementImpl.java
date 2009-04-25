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
import com.espertech.esper.collection.SafeIteratorImpl;
import com.espertech.esper.collection.SingleEventIterator;
import com.espertech.esper.dispatch.DispatchService;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.timer.TimeSourceService;
import com.espertech.esper.view.Viewable;

import java.util.Iterator;
import java.util.ArrayList;

/**
 * Statement implementation for EPL statements.
 */
public class EPStatementImpl implements EPStatementSPI
{
    private final EPStatementListenerSet statementListenerSet;
    private final String statementId;
    private final String statementName;
    private final String expressionText;
    private boolean isPattern;
    private UpdateDispatchViewBase dispatchChildView;
    private StatementLifecycleSvc statementLifecycleSvc;
    private VariableService variableService;

    private long timeLastStateChange;
    private Viewable parentView;
    private EPStatementState currentState;
    private EventType eventType;
    private EPStatementHandle epStatementHandle;
    private StatementResultService statementResultService;
    private StatementMetadata statementMetadata;
    private Object userObject;

    /**
     * Ctor.
     * @param statementId is a unique ID assigned by the engine for the statement
     * @param statementName is the statement name assigned during creation, or the statement id if none was assigned
     * @param expressionText is the EPL and/or pattern expression
     * @param isPattern is true to indicate this is a pure pattern expression
     * @param dispatchService for dispatching events to listeners to the statement
     * @param statementLifecycleSvc handles lifecycle transitions for the statement
     * @param isBlockingDispatch is true if the dispatch to listeners should block to preserve event generation order
     * @param isSpinBlockingDispatch true to use spin locks blocking to deliver results, as locks are usually uncontended
     * @param msecBlockingTimeout is the max number of milliseconds of block time
     * @param timeLastStateChange the timestamp the statement was created and started
     * @param epStatementHandle the handle and statement lock associated with the statement
     * @param variableService provides access to variable values
     * @param statementResultService handles statement result generation
     * @param timeSourceService time source provider
     * @param statementMetadata statement metadata
     * @param userObject the application define user object associated to each statement, if supplied
     */
    public EPStatementImpl(String statementId,
                              String statementName,
                              String expressionText,
                              boolean isPattern,
                              DispatchService dispatchService,
                              StatementLifecycleSvc statementLifecycleSvc,
                              long timeLastStateChange,
                              boolean isBlockingDispatch,
                              boolean isSpinBlockingDispatch,
                              long msecBlockingTimeout,
                              EPStatementHandle epStatementHandle,
                              VariableService variableService,
                              StatementResultService statementResultService,
                              TimeSourceService timeSourceService,
                              StatementMetadata statementMetadata,
                              Object userObject)
    {
        this.isPattern = isPattern;
        this.statementId = statementId;
        this.statementName = statementName;
        this.expressionText = expressionText;
        this.statementLifecycleSvc = statementLifecycleSvc;
        statementListenerSet = new EPStatementListenerSet();
        if (isBlockingDispatch)
        {
            if (isSpinBlockingDispatch)
            {
                this.dispatchChildView = new UpdateDispatchViewBlockingSpin(statementResultService, dispatchService, msecBlockingTimeout, timeSourceService);
            }
            else
            {
                this.dispatchChildView = new UpdateDispatchViewBlockingWait(statementResultService, dispatchService, msecBlockingTimeout);
            }
        }
        else
        {
            this.dispatchChildView = new UpdateDispatchViewNonBlocking(statementResultService, dispatchService);
        }
        this.currentState = EPStatementState.STOPPED;
        this.timeLastStateChange = timeLastStateChange;
        this.epStatementHandle = epStatementHandle;
        this.variableService = variableService;
        this.statementResultService = statementResultService;
        this.statementMetadata = statementMetadata;
        this.userObject = userObject;
        statementResultService.setUpdateListeners(statementListenerSet);
    }

    public String getStatementId()
    {
        return statementId;
    }

    public void start()
    {
        if (statementLifecycleSvc == null)
        {
            throw new IllegalStateException("Cannot start statement, statement is in destroyed state");
        }
        statementLifecycleSvc.start(statementId);
    }

    public void stop()
    {
        if (statementLifecycleSvc == null)
        {
            throw new IllegalStateException("Cannot stop statement, statement is in destroyed state");
        }
        statementLifecycleSvc.stop(statementId);

        // On stop, we give the dispatch view a chance to dispatch final results, if any
        statementResultService.dispatchOnStop();

        dispatchChildView.clear();
    }

    public void destroy()
    {
        if (currentState == EPStatementState.DESTROYED)
        {
            throw new IllegalStateException("Statement already destroyed");
        }
        statementLifecycleSvc.destroy(statementId);
        parentView = null;
        eventType = null;
        dispatchChildView = null;
        statementLifecycleSvc = null;
    }

    public EPStatementState getState()
    {
        return currentState;
    }

    public void setCurrentState(EPStatementState currentState, long timeLastStateChange)
    {
        this.currentState = currentState;
        this.timeLastStateChange = timeLastStateChange;
    }

    public void setParentView(Viewable viewable)
    {
        if (viewable == null)
        {
            if (parentView != null)
            {
                parentView.removeView(dispatchChildView);
            }
            parentView = null;
        }
        else
        {
            parentView = viewable;
            parentView.addView(dispatchChildView);
            eventType = parentView.getEventType();
        }
    }

    public String getText()
    {
        return expressionText;
    }

    public String getName()
    {
        return statementName;
    }

    public Iterator<EventBean> iterator()
    {
        // Return null if not started
        variableService.setLocalVersion();
        if (parentView == null)
        {
            return null;
        }
        if (isPattern)
        {
            return new SingleEventIterator(statementResultService.getLastIterableEvent());
        }
        else
        {
            return parentView.iterator();
        }
    }

    public SafeIterator<EventBean> safeIterator()
    {
        // Return null if not started
        if (parentView == null)
        {
            return null;
        }

        // Set variable version and acquire the lock first
        epStatementHandle.getStatementLock().acquireLock(null);
        try
        {
            variableService.setLocalVersion();

            // Provide iterator - that iterator MUST be closed else the lock is not released
            if (isPattern)
            {
                return new SafeIteratorImpl<EventBean>(epStatementHandle.getStatementLock(), dispatchChildView.iterator());
            }
            else
            {
                return new SafeIteratorImpl<EventBean>(epStatementHandle.getStatementLock(), parentView.iterator());
            }
        }
        catch (RuntimeException ex)
        {
            epStatementHandle.getStatementLock().releaseLock(null);
            throw ex;
        }
    }

    public EventType getEventType()
    {
        return eventType;
    }

    /**
     * Returns the set of listeners to the statement.
     * @return statement listeners
     */
    public EPStatementListenerSet getListenerSet()
    {
        return statementListenerSet;
    }

    public void setListeners(EPStatementListenerSet listenerSet)
    {
        statementListenerSet.setListeners(listenerSet);
        statementResultService.setUpdateListeners(listenerSet);
    }

    /**
     * Add a listener to the statement.
     * @param listener to add
     */
    public void addListener(UpdateListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Null listener reference supplied");
        }

        statementListenerSet.addListener(listener);
        statementResultService.setUpdateListeners(statementListenerSet);
        statementLifecycleSvc.dispatchStatementLifecycleEvent(
                new StatementLifecycleEvent(this, StatementLifecycleEvent.LifecycleEventType.LISTENER_ADD, listener));
    }

    public void addListenerWithReplay(UpdateListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Null listener reference supplied");
        }

        if (isDestroyed())
        {
            throw new IllegalStateException("Statement is in destroyed state");
        }

        epStatementHandle.getStatementLock().acquireLock(null);
        try
        {
            // Add listener - listener not receiving events from this statement, as the statement is locked
            statementListenerSet.addListener(listener);
            statementResultService.setUpdateListeners(statementListenerSet);
            statementLifecycleSvc.dispatchStatementLifecycleEvent(
                    new StatementLifecycleEvent(this, StatementLifecycleEvent.LifecycleEventType.LISTENER_ADD, listener));

            Iterator<EventBean> it = iterator();
            if (it == null)
            {
                listener.update(null, null);
                return;
            }

            ArrayList<EventBean> events = new ArrayList<EventBean>();
            for (; it.hasNext();)
            {
                events.add(it.next());
            }

            if (events.isEmpty())
            {
                listener.update(null, null);
            }
            else
            {
                EventBean[] iteratorResult = events.toArray(new EventBean[events.size()]);
                listener.update(iteratorResult, null);
            }
        }
        finally
        {
            epStatementHandle.getStatementLock().releaseLock(null);
        }
    }

    /**
     * Remove a listeners to a statement.
     * @param listener to remove
     */
    public void removeListener(UpdateListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Null listener reference supplied");
        }

        statementListenerSet.removeListener(listener);
        statementResultService.setUpdateListeners(statementListenerSet);
        statementLifecycleSvc.dispatchStatementLifecycleEvent(
                new StatementLifecycleEvent(this, StatementLifecycleEvent.LifecycleEventType.LISTENER_REMOVE, listener));
    }

    /**
     * Remove all listeners to a statement.
     */
    public void removeAllListeners()
    {
        statementListenerSet.removeAllListeners();
        statementResultService.setUpdateListeners(statementListenerSet);
        statementLifecycleSvc.dispatchStatementLifecycleEvent(
                new StatementLifecycleEvent(this, StatementLifecycleEvent.LifecycleEventType.LISTENER_REMOVE_ALL));
    }

    public void addListener(StatementAwareUpdateListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Null listener reference supplied");
        }

        statementListenerSet.addListener(listener);
        statementResultService.setUpdateListeners(statementListenerSet);
        //statementLifecycleSvc.updatedListeners(statementId, statementName, statementListenerSet);
        statementLifecycleSvc.dispatchStatementLifecycleEvent(
                new StatementLifecycleEvent(this, StatementLifecycleEvent.LifecycleEventType.LISTENER_ADD, listener));
    }

    public void removeListener(StatementAwareUpdateListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Null listener reference supplied");
        }

        statementListenerSet.removeListener(listener);
        statementResultService.setUpdateListeners(statementListenerSet);
        statementLifecycleSvc.dispatchStatementLifecycleEvent(
                new StatementLifecycleEvent(this, StatementLifecycleEvent.LifecycleEventType.LISTENER_REMOVE, listener));
    }

    public Iterator<StatementAwareUpdateListener> getStatementAwareListeners()
    {
        return statementListenerSet.getStmtAwareListeners().iterator();
    }

    public Iterator<UpdateListener> getUpdateListeners()
    {
        return statementListenerSet.getListeners().iterator();
    }

    public long getTimeLastStateChange()
    {
        return timeLastStateChange;
    }

    public boolean isStarted()
    {
        return currentState == EPStatementState.STARTED;
    }

    public boolean isStopped()
    {
        return currentState == EPStatementState.STOPPED;
    }

    public boolean isDestroyed()
    {
        return currentState == EPStatementState.DESTROYED;
    }

    public void setSubscriber(Object subscriber)
    {
        statementListenerSet.setSubscriber(subscriber);
        statementResultService.setUpdateListeners(statementListenerSet);
    }

    public Object getSubscriber()
    {
        return statementListenerSet.getSubscriber();
    }

    public boolean isPattern() {
        return isPattern;
    }

    public StatementMetadata getStatementMetadata()
    {
        return statementMetadata;
    }

    public Object getUserObject()
    {
        return userObject;
    }
}
