/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.internal;

import com.espertech.esper.client.EventBean;

/**
 * Observer interface to a stream publishing new and old events.
 */
public interface LastPostObserver
{
    /**
     * Receive new and old events from a stream.
     * @param streamId - the stream number sending the events
     * @param newEvents - new events
     * @param oldEvents - old events
     */
    public void newData(int streamId, EventBean[] newEvents, EventBean[] oldEvents);
}
