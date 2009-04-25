/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventBean;

import java.util.Collection;

/**
 * Interface for filtering events by event type and event property values. Allows adding and removing filters.
 * <p>
 * Filters are defined by a {@link FilterSpecCompiled} and are associated with a {@link FilterHandle}
 * callback.
 * Implementations may decide if the same filter callback can be registered twice for different or some
 * filter specifications.
 * <p>
 * The performance of an implementation of this service is crucial in achieving a high overall event throughput.
 */
public interface FilterService
{
    /**
     * Finds matching filters to the event passed in and invokes their associated callback method.
     * @param event is the event to be matched against filters
     * @param matches is a collection that is populated via add method with any handles for matching filters
     */
    public void evaluate(EventBean event, Collection<FilterHandle> matches);

    /**
     * Add a filter for events as defined by the filter specification, and register a
     * callback to be invoked upon evaluation of an event that matches the filter spec.
     * @param filterValueSet is a specification of filter parameters, contains
     * event type information, event property values and operators
     * @param callback is the callback to be invoked when the filter matches an event
     */
    public void add(FilterValueSet filterValueSet, FilterHandle callback);

    /**
     * Remove a filter callback.
     * @param callback is the callback to be removed
     */
    public void remove(FilterHandle callback);

    /**
     * Return a count of the number of events evaluated by this service.
     * @return count of invocations of evaluate method
     */
    public long getNumEventsEvaluated();

    /**
     * Reset the number of events evaluated
     */
    public void resetStats();

    /**
     * Destroy the service.
     */
    public void destroy();
}
