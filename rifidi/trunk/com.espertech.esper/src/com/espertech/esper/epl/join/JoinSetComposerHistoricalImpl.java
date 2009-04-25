/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join;

import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.db.DataCacheClearableMap;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.view.Viewable;
import com.espertech.esper.view.HistoricalEventViewable;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implements the function to determine a join result set using tables/indexes and query strategy
 * instances for each stream.
 */
public class JoinSetComposerHistoricalImpl implements JoinSetComposer
{
    private final EventTable[][] repositories;
    private final QueryStrategy[] queryStrategies;

    // Set semantic eliminates duplicates in result set, use Linked set to preserve order
    private Set<MultiKey<EventBean>> oldResults = new LinkedHashSet<MultiKey<EventBean>>();
    private Set<MultiKey<EventBean>> newResults = new LinkedHashSet<MultiKey<EventBean>>();
    private EventTable[][] tables = new EventTable[0][];
    private Viewable[] streamViews;

    /**
     * Ctor.
     * @param repositories indexes for non-historical streams
     * @param queryStrategies for each stream a strategy to execute the join
     * @param streamViews the viewable representing each stream
     */
    public JoinSetComposerHistoricalImpl(EventTable[][] repositories, QueryStrategy[] queryStrategies, Viewable[] streamViews)
    {
        this.repositories = repositories;
        this.queryStrategies = queryStrategies;
        this.streamViews = streamViews;
    }

    public void init(EventBean[][] eventsPerStream)
    {
        if (repositories == null)
        {
            return;
        }

        for (int i = 0; i < eventsPerStream.length; i++)
        {
            if ((eventsPerStream[i] != null) && (repositories[i] != null))
            {
                for (int j = 0; j < repositories[i].length; j++)
                {
                    repositories[i][j].add((eventsPerStream[i]));
                }
            }
        }
    }

    public void destroy()
    {
        if (repositories == null)
        {
            return;
        }

        for (int i = 0; i < repositories.length; i++)
        {
            if (repositories[i] != null)
            {
                for (EventTable table : repositories[i])
                {
                    table.clear();
                }
            }
        }
    }

    public UniformPair<Set<MultiKey<EventBean>>> join(EventBean[][] newDataPerStream, EventBean[][] oldDataPerStream)
    {
        oldResults.clear();
        newResults.clear();

        // join old data
        for (int i = 0; i < oldDataPerStream.length; i++)
        {
            if (oldDataPerStream[i] != null)
            {
                queryStrategies[i].lookup(oldDataPerStream[i], oldResults);
            }
        }

        // add new data to indexes
        if (repositories != null)
        {
            for (int i = 0; i < newDataPerStream.length; i++)
            {
                if (newDataPerStream[i] != null)
                {
                    for (int j = 0; j < repositories[i].length; j++)
                    {
                        repositories[i][j].add((newDataPerStream[i]));
                    }
                }
            }

            // remove old data from indexes
            // adding first and then removing as the events added may be remove right away
            for (int i = 0; i < oldDataPerStream.length; i++)
            {
                if (oldDataPerStream[i] != null)
                {
                    for (int j = 0; j < repositories[i].length; j++)
                    {
                        repositories[i][j].remove(oldDataPerStream[i]);
                    }
                }
            }
        }

        // join new data
        for (int i = 0; i < newDataPerStream.length; i++)
        {
            if (newDataPerStream[i] != null)
            {
                queryStrategies[i].lookup(newDataPerStream[i], newResults);
            }
        }

        return new UniformPair<Set<MultiKey<EventBean>>>(newResults, oldResults);
    }

    /**
     * Returns tables.
     * @return tables for stream.
     */
    protected EventTable[][] getTables()
    {
        return tables;
    }

    /**
     * Returns query strategies.
     * @return query strategies
     */
    protected QueryStrategy[] getQueryStrategies()
    {
        return queryStrategies;
    }

    public Set<MultiKey<EventBean>> staticJoin()
    {
        Set<MultiKey<EventBean>> result = new LinkedHashSet<MultiKey<EventBean>>();
        EventBean[] lookupEvents = new EventBean[1];

        // Assign a local cache for the thread's evaluation of the join
        // This ensures that if a SQL/method generates a row for a result set based on an input parameter, the event instance is the same
        // in the join, and thus the same row does not appear twice.
        DataCacheClearableMap caches[] = new DataCacheClearableMap[queryStrategies.length];
        assignThreadLocalCache(streamViews, caches);

        // perform join
        try
        {
            // for each stream, perform query strategy
            for (int stream = 0; stream < queryStrategies.length; stream++)
            {
                if (streamViews[stream] instanceof HistoricalEventViewable)
                {
                    HistoricalEventViewable historicalViewable = (HistoricalEventViewable) streamViews[stream];
                    if (historicalViewable.hasRequiredStreams())
                    {
                        continue;
                    }

                    // there may not be a query strategy since only a full outer join may need to consider all rows
                    if (queryStrategies[stream] != null)
                    {
                        Iterator<EventBean> streamEvents = historicalViewable.iterator();
                        for (;streamEvents.hasNext();)
                        {
                            lookupEvents[0] = streamEvents.next();
                            queryStrategies[stream].lookup(lookupEvents, result);
                        }
                    }
                }
                else
                {
                    Iterator<EventBean> streamEvents = streamViews[stream].iterator();
                    for (;streamEvents.hasNext();)
                    {
                        lookupEvents[0] = streamEvents.next();
                        queryStrategies[stream].lookup(lookupEvents, result);
                    }
                }
            }
        }
        finally
        {
            deassignThreadLocalCache(streamViews, caches);
        }

        return result;
    }

    private void assignThreadLocalCache(Viewable[] streamViews, DataCacheClearableMap[] caches)
    {
        for (int stream = 0; stream < streamViews.length; stream++)
        {
            if (streamViews[stream] instanceof HistoricalEventViewable)
            {
                HistoricalEventViewable historicalViewable = (HistoricalEventViewable) streamViews[stream];
                caches[stream] = new DataCacheClearableMap();
                historicalViewable.getDataCacheThreadLocal().set(caches[stream]);
            }
        }
    }

    private void deassignThreadLocalCache(Viewable[] streamViews, DataCacheClearableMap[] caches)
    {
        for (int stream = 0; stream < streamViews.length; stream++)
        {
            if (streamViews[stream] instanceof HistoricalEventViewable)
            {
                HistoricalEventViewable historicalViewable = (HistoricalEventViewable) streamViews[stream];
                historicalViewable.getDataCacheThreadLocal().set(null);
                caches[stream].clear();
            }
        }
    }
}
