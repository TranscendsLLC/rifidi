/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

import com.espertech.esper.client.EventBean;

/**
 * Interface to receive notification from an engine that an event that has been sent into the engine or that
 * has been generated via insert-into has not been matched to any statement, considering all started statement's
 * event stream filter criteria (not considering where-clause and having-clauses).
 * <p>
 * Also see {@link EPRuntime#setUnmatchedListener(UnmatchedListener)}.
 */
public interface UnmatchedListener
{
    /**
     * Called upon the engine encountering an event not matched to any of the event stream filters
     * of any active statement.
     * @param event unmatched event
     */
    public void update(EventBean event);
}
