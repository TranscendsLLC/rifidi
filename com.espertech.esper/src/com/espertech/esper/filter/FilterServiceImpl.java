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
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the filter service interface.
 * Does not allow the same filter callback to be added more then once.
 */
public final class FilterServiceImpl implements FilterService
{
    private static final Log log = LogFactory.getLog(FilterServiceImpl.class);
    private final EventTypeIndexBuilder indexBuilder;
    private final EventTypeIndex eventTypeIndex;
    private final AtomicLong numEventsEvaluated = new AtomicLong();

    /**
     * Constructor.
     */
    protected FilterServiceImpl()
    {
        eventTypeIndex = new EventTypeIndex();
        indexBuilder = new EventTypeIndexBuilder(eventTypeIndex);
    }

    public void destroy()
    {
        log.debug("Destroying filter service");
        eventTypeIndex.destroy();
        indexBuilder.destroy();
    }

    public final void add(FilterValueSet filterValueSet, FilterHandle filterCallback)
    {
        indexBuilder.add(filterValueSet, filterCallback);
    }

    public final void remove(FilterHandle filterCallback)
    {
        indexBuilder.remove(filterCallback);
    }

    public final void evaluate(EventBean eventBean, Collection<FilterHandle> matches)
    {
        numEventsEvaluated.incrementAndGet();

        // Finds all matching filters and return their callbacks
        eventTypeIndex.matchEvent(eventBean, matches);
    }

    public final long getNumEventsEvaluated()
    {
        return numEventsEvaluated.get();
    }

    public void resetStats() {
        numEventsEvaluated.set(0);
    }
}
