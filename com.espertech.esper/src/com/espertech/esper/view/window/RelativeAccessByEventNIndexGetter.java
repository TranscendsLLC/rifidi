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

import java.util.Map;
import java.util.HashMap;

/**
 * Provides random-access into window contents by event and index as a combination.
 */
public class RelativeAccessByEventNIndexGetter implements IStreamRelativeAccess.IStreamRelativeAccessUpdateObserver
{
    private final Map<EventBean, IStreamRelativeAccess> accessorByEvent = new HashMap<EventBean, IStreamRelativeAccess>();
    private final Map<IStreamRelativeAccess, EventBean[]> eventsByAccessor  = new HashMap<IStreamRelativeAccess, EventBean[]>();

    public void updated(IStreamRelativeAccess iStreamRelativeAccess, EventBean[] newData)
    {
        // remove data posted from the last update
        EventBean[] lastNewData = eventsByAccessor.get(iStreamRelativeAccess);
        if (lastNewData != null)
        {
            for (int i = 0; i < lastNewData.length; i++)
            {
                accessorByEvent.remove(lastNewData[i]);
            }
        }

        if (newData == null)
        {
            return;
        }

        // hold accessor per event for querying
        for (int i = 0; i < newData.length; i++)
        {
            accessorByEvent.put(newData[i], iStreamRelativeAccess);
        }

        // save new data for access to later removal
        eventsByAccessor.put(iStreamRelativeAccess, newData);
    }

    /**
     * Returns the access into window contents given an event.
     * @param event to which the method returns relative access from
     * @return buffer
     */
    public IStreamRelativeAccess getAccessor(EventBean event)
    {
        IStreamRelativeAccess iStreamRelativeAccess = accessorByEvent.get(event);
        if (iStreamRelativeAccess == null)
        {
            throw new IllegalStateException("Accessor for window random access not found for event " + event);
        }
        return iStreamRelativeAccess;
    }
}
