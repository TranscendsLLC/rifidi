/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKey;

import java.util.Set;

/**
 * Encapsulates the strategy use to resolve the events for a stream into a tuples of events in a join.
 */
public interface QueryStrategy
{
    /**
     * Look up events returning tuples of joined events.
     * @param lookupEvents - events to use to perform the join
     * @param joinSet - result join tuples of events
     */
    public void lookup(EventBean[] lookupEvents, Set<MultiKey<EventBean>> joinSet);
}
