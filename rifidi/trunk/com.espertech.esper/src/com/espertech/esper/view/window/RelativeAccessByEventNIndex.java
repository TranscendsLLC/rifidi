/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

import com.espertech.esper.client.EventBean;

/**
 * Provides access to prior events given an event from which to count back, and an index to look at.
 */
public interface RelativeAccessByEventNIndex
{
    /**
     * Returns the prior event to the given event counting back the number of events as supplied by index.
     * @param event is the event to count back from
     * @param index is the number of events to go back
     * @return event
     */
    public EventBean getRelativeToEvent(EventBean event, int index);
}
