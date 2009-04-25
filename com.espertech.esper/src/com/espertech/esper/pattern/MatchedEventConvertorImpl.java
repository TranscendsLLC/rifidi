/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.map.MapEventBean;
import com.espertech.esper.collection.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;

/**
 * Implements a convertor for pattern partial results to events per stream.  
 */
public class MatchedEventConvertorImpl implements MatchedEventConvertor
{
    private final EventBean[] eventsPerStream;
    private final LinkedHashMap<String, Pair<EventType, String>> filterTypes;
    private final LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes;

    /**
     * Ctor.
     * @param filterTypes the filter one-event types
     * @param arrayEventTypes the filter many-event types
     * @param eventAdapterService for creating wrappers if required
     */
    public MatchedEventConvertorImpl(LinkedHashMap<String, Pair<EventType, String>> filterTypes, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes, EventAdapterService eventAdapterService)
    {
        int size = filterTypes.size();
        if (arrayEventTypes != null)
        {
            size += arrayEventTypes.size();
        }

        this.eventsPerStream = new EventBean[size];
        this.filterTypes = new LinkedHashMap<String, Pair<EventType, String>>(filterTypes);
        this.arrayEventTypes = new LinkedHashMap<String, Pair<EventType, String>>(arrayEventTypes);
    }

    public EventBean[] convert(MatchedEventMap events)
    {
        int count = 0;
        for (Map.Entry<String, Pair<EventType, String>> entry : filterTypes.entrySet())
        {
            EventBean event = events.getMatchingEvent(entry.getKey());
            eventsPerStream[count++] = event;
        }
        if (arrayEventTypes != null)
        {
            for (Map.Entry<String, Pair<EventType, String>> entry : arrayEventTypes.entrySet())
            {
                EventBean[] eventArray = (EventBean[]) events.getMatchingEventAsObject(entry.getKey());
                HashMap map = new HashMap();
                map.put(entry.getKey(), eventArray);
                EventBean event = new MapEventBean(map, null);
                eventsPerStream[count++] = event;
            }
        }
        return eventsPerStream;
    }
}
