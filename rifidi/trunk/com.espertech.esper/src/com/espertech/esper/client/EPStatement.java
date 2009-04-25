/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

/**
 * Statement interface that provides methods to start, stop and destroy a statement as well as
 * get statement information such as statement name, expression text and current state.
 * <p>
 * Statements have 3 states: STARTED, STOPPED and DESTROYED.
 * <p>
 * In started state, statements are actively evaluating event streams according to the statement expression. Started
 * statements can be stopped and destroyed.
 * <p>
 * In stopped state, statements are inactive. Stopped statements can either be started, in which case
 * they begin to actively evaluate event streams, or destroyed.
 * <p>
 * Destroyed statements have relinguished all statement resources and cannot be started or stopped. 
 */
public interface EPStatement extends EPListenable, EPIterable
{
    /**
     * Start the statement.
     */
    public void start();

    /**
     * Stop the statement.
     */
    public void stop();

    /**
     * Destroy the statement releasing all statement resources.
     * <p>
     * A destroyed statement cannot be started again.
     */
    public void destroy();

    /**
     * Returns the statement's current state.
     * @return state enum
     */
    public EPStatementState getState();

    /**
     * Returns true if the statement state is started.
     * @return true for started statements, false for stopped or destroyed statements.
     */
    public boolean isStarted();

    /**
     * Returns true if the statement state is stopped.
     * @return true for stopped statements, false for started or destroyed statements.
     */
    public boolean isStopped();

    /**
     * Returns true if the statement state is destroyed.
     * @return true for destroyed statements, false for started or stopped statements.
     */
    public boolean isDestroyed();

    /**
     * Returns the underlying expression text.
     * @return expression text
     */
    public String getText();

    /**
     * Returns the statement name.
     * @return statement name
     */
    public String getName();

    /**
     * Returns the system time in milliseconds of when the statement last change state.
     * @return time in milliseconds of last statement state change
     */
    public long getTimeLastStateChange();

    /**
     * Attaches a subscriber to receive statement results,
     * or removes a previously set subscriber (by providing a null value).
     * @param subscriber to attach, or null to remove the previously set subscriber
     * @throws EPSubscriberException if the subscriber does not provide the methods
     *   needed to receive statement results
     */
    public void setSubscriber(Object subscriber) throws EPSubscriberException;

    /**
     * Returns the current subscriber instance that receives statement results.
     * @return subscriber object, or null to indicate that no subscriber is attached
     * @throws EPSubscriberException if the subscriber does not provide the methods
     *   needed to receive statement results
     */
    public Object getSubscriber();

    /**
     * Returns true if statement is a pattern
     * @return true if statement is a pattern
     */
    public boolean isPattern();

    /**
     * Returns the application defined user data object associated with the statement, or null if none
     * was supplied at time of statement creation.
     * <p>
     * The <em>user object</em> is a single, unnamed field that is stored with every statement.
     * Applications may put arbitrary objects in this field or a null value.
     * <p>
     * User objects are passed at time of statement creation as a parameter the create method. 
     * @return user object or null if none defined
     */
    public Object getUserObject();

    /**
     * Add an update listener replaying current statement results to the listener.
     * <p>
     * The listener receives current statement results as the first call to the update method
     * of the listener, passing in the newEvents parameter the current statement results as an array of zero or more events.
     * Subsequent calls to the update method of the listener are statement results.   
     * <p>
     * Current statement results are the events returned by the iterator or safeIterator methods.
     * <p>
     * Delivery of current statement results in the first call is performed by the same thread invoking this method,
     * while subsequent calls to the listener may deliver statement results by the same or other threads.
     * <p>
     * Note: this is a blocking call, delivery is atomic: Events occurring during iteration and
     * delivery to the listener are guaranteed to be delivered in a separate call and not lost.
     * The listener implementation should minimize long-running or blocking operations.
     * <p>
     * Delivery is only atomic relative to the current statement. If the same listener instance is
     * registered with other statements it may receive other statement result
     * s simultaneously.
     * <p>
     * If a statement is not started an therefore does not have current results, the listener
     * receives a single invocation with a null value in newEvents.  
     * @param listener to add
     */
    public void addListenerWithReplay(UpdateListener listener);
}