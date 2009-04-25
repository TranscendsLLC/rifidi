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

import java.util.ArrayList;

/**
 * For use with length and time window views that must provide random access into data window contents
 * provided for the "previous" expression if used.
 */
public class IStreamRandomAccess implements RandomAccessByIndex, ViewUpdatedCollection
{
    private ArrayList<EventBean> arrayList;
    private final RandomAccessByIndexObserver updateObserver;

    /**
     * Ctor.
     * @param updateObserver is invoked when updates are received
     */
    public IStreamRandomAccess(RandomAccessByIndexObserver updateObserver)
    {
        this.updateObserver = updateObserver;
        this.arrayList = new ArrayList<EventBean>();
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        updateObserver.updated(this);
        if (newData != null)
        {
            for (int i = 0; i < newData.length; i++)
            {
                arrayList.add(0, newData[i]);
            }
        }

        if (oldData != null)
        {
            for (int i = 0; i < oldData.length; i++)
            {
                arrayList.remove(arrayList.size() - 1);
            }
        }
    }

    public EventBean getNewData(int index)
    {
        // New events are added to the start of the list
        if (index < arrayList.size() )
        {
            return arrayList.get(index);
        }
        return null;
    }

    public EventBean getOldData(int index)
    {
        return null;
    }

    public void destroy()
    {
        // No action required
    }
}
