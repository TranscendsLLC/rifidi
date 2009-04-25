/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.schedule;

import java.util.Collection;

/**
 * Interface for a service that allows to add and remove handles (typically storing callbacks)
 * for a certain time which are returned when
 * the evaluate method is invoked and the current time is on or after the handle's registered time.
 * It is the expectation that the setTime method is called
 * with same or ascending values for each subsequent call. Handles with are triggered are automatically removed
 * by implementations.
 */
public interface SchedulingService extends TimeProvider
{
    /**
     * Add a callback for after the given milliseconds from the current time.
     * If the same callback (equals) was already added before, the method will not add a new
     * callback or change the existing callback to a new time, but throw an exception.
     * @param afterMSec number of millisec to get a callback
     * @param handle to add
     * @param slot allows ordering of concurrent callbacks
     * @throws ScheduleServiceException thrown if the add operation did not complete
     * @throws ScheduleHandleExistsException to indicate the handle already existed
     */
    public void add(long afterMSec, ScheduleHandle handle, ScheduleSlot slot)
            throws ScheduleServiceException;

    /**
     * Add a callback for a time specified by the schedule specification passed in based on the current time.
     * If the same callback (equals) was already added before, the method will not add a new
     * callback or change the existing callback to a new time, but throw an exception.
     * @param scheduleSpec holds the crontab-like information defining the next occurance
     * @param handle to add
     * @param slot allows ordering of concurrent callbacks
     * @throws ScheduleServiceException thrown if the add operation did not complete
     */
    public void add(ScheduleSpec scheduleSpec, ScheduleHandle handle, ScheduleSlot slot)
            throws ScheduleServiceException;

    /**
     * Remove a handle.
     * If the handle to be removed was not found an exception is thrown.
     * @param handle to remove
     * @param slot for which the callback was added
     * @throws ScheduleServiceException thrown if the callback was not located
     */
    public void remove(ScheduleHandle handle, ScheduleSlot slot)
            throws ScheduleServiceException;

    /**
     * Set the time based upon which the evaluation of events invokes callbacks.
     * @param timestamp to set
     */
    public void setTime(long timestamp);

    /**
     * Evaluate the current time and add to the collection any handles scheduled for execution.
     * @param handles is a collection of handles populated by the service with any callbacks due
     * for the current time
     */
    public void evaluate(Collection<ScheduleHandle> handles);

    /**
     * Returns a bucket from which slots can be allocated for ordering concurrent callbacks.
     * @return bucket
     */
    public ScheduleBucket allocateBucket();

    /**
     * Destroy the service.
     */
    public void destroy();

    /**
     * Returns time handle count.
     * @return count
     */
    public int getTimeHandleCount();

    /**
     * Returns furthest in the future handle.
     * @return future handle
     */
    public Long getFurthestTimeHandle();

    /**
     * Returns count of handles.
     * @return count
     */
    public int getScheduleHandleCount();

    /**
     * Returns true if the handle has been scheduled already.
     * @param handle to check
     * @return indicator whether the handle is in use
     */
    public boolean isScheduled(ScheduleHandle handle);
}


