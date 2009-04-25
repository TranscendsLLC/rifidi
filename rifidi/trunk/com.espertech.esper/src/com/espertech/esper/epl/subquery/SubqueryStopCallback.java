/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.subquery;

import com.espertech.esper.util.StopCallback;
import com.espertech.esper.epl.join.table.EventTable;

/**
 * Implements a stop callback for use with subqueries to clear their indexes
 * when a statement is stopped.
 */
public class SubqueryStopCallback implements StopCallback
{
    private final EventTable eventIndex;

    /**
     * Ctor.
     * @param eventIndex index to clear
     */
    public SubqueryStopCallback(EventTable eventIndex) {
        this.eventIndex = eventIndex;
    }

    // Clear out index on statement stop
    public void stop()
    {
        if (eventIndex != null)
        {
            eventIndex.clear();
        }
    }
}