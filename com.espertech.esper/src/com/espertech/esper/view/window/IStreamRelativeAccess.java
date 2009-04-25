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
import com.espertech.esper.collection.ViewUpdatedCollection;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides relative access to insert stream events for certain window.
 */
public class IStreamRelativeAccess implements RelativeAccessByEventNIndex, ViewUpdatedCollection
{
    private final Map<EventBean, Integer> indexPerEvent;
    private EventBean[] lastNewData;
    private final IStreamRelativeAccessUpdateObserver updateObserver;

    /**
     * Ctor.
     * @param updateObserver is invoked when updates are received
     */
    public IStreamRelativeAccess(IStreamRelativeAccessUpdateObserver updateObserver)
    {
        this.updateObserver = updateObserver;
        indexPerEvent = new HashMap<EventBean, Integer>();
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        updateObserver.updated(this, newData);
        indexPerEvent.clear();
        lastNewData = newData;

        if (newData != null)
        {
            for (int i = 0; i < newData.length; i++)
            {
                indexPerEvent.put(newData[i], i);
            }
        }
    }

    public EventBean getRelativeToEvent(EventBean event, int prevIndex)
    {
        if (lastNewData == null)
        {
            return null;
        }

        if (prevIndex == 0)
        {
            return event;
        }

        Integer indexIncoming = indexPerEvent.get(event);
        if (indexIncoming == null)
        {
            return null;
        }

        if (prevIndex > indexIncoming)
        {
            return null;
        }

        int relativeIndex = indexIncoming - prevIndex;
        if ((relativeIndex < lastNewData.length) && (relativeIndex >= 0))
        {
            return lastNewData[relativeIndex];
        }
        return null;
    }

    /**
     * For indicating that the collection has been updated.
     */
    public interface IStreamRelativeAccessUpdateObserver
    {
        /**
         * Callback to indicate an update.
         * @param iStreamRelativeAccess is the collection
         * @param newData is the new data available
         */
        public void updated(IStreamRelativeAccess iStreamRelativeAccess, EventBean[] newData);
    }

    public void destroy()
    {
        // No action required
    }
}
