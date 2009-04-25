/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.table;

import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.join.HistoricalIndexLookupStrategy;
import com.espertech.esper.epl.join.JoinSetComposerFactoryImpl;
import com.espertech.esper.epl.join.PollResultIndexingStrategy;
import com.espertech.esper.epl.join.plan.QueryGraph;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.JavaClassHelper;

import java.util.*;

/**
 * Manages index-building and sharing for historical streams by collecting required indexes during the
 * query planning phase, and by providing the right lookup strategy and indexing strategy during
 * query execution node creation.
 */
public class HistoricalStreamIndexList
{
    private final int historicalStreamNum;
    private final EventType[] typesPerStream;
    private final QueryGraph queryGraph;
    private final TreeSet<Integer> pollingStreams;

    private Map<HistoricalStreamIndexDesc, List<Integer>> indexesUsedByStreams;
    private PollResultIndexingStrategy masterIndexingStrategy;

    /**
     * Ctor.
     * @param historicalStreamNum number of the historical stream
     * @param typesPerStream event types for each stream
     * @param queryGraph relationship between key and index properties
     */
    public HistoricalStreamIndexList(int historicalStreamNum, EventType[] typesPerStream, QueryGraph queryGraph)
    {
        this.historicalStreamNum = historicalStreamNum;
        this.typesPerStream = typesPerStream;
        this.queryGraph = queryGraph;
        this.pollingStreams = new TreeSet<Integer>();
    }

    /**
     * Used during query plan phase to indicate that an index must be provided for use in lookup of historical events by using a
     * stream's events.
     * @param streamViewStreamNum the stream providing lookup events
     */
    public void addIndex(int streamViewStreamNum)
    {
        pollingStreams.add(streamViewStreamNum);
    }

    /**
     * Get the strategies to use for polling from a given stream.
     * @param streamViewStreamNum the stream providing the polling events
     * @return looking and indexing strategy
     */
    public Pair<HistoricalIndexLookupStrategy, PollResultIndexingStrategy> getStrategy(int streamViewStreamNum)
    {
        // If there is only a single polling stream, then build a single index
        if (pollingStreams.size() == 1)
        {
            return JoinSetComposerFactoryImpl.determineIndexing(queryGraph, typesPerStream[historicalStreamNum], typesPerStream[streamViewStreamNum], historicalStreamNum, streamViewStreamNum);
        }

        // If there are multiple polling streams, determine if a single index is appropriate.
        // An index can be reused if:
        //  (a) indexed property names are the same
        //  (b) indexed property types are the same
        //  (c) key property types are the same (because of coercion)
        // A index lookup strategy is always specific to the providing stream.
        if (indexesUsedByStreams == null)
        {
            indexesUsedByStreams = new LinkedHashMap<HistoricalStreamIndexDesc, List<Integer>>();
            for (int pollingStream : pollingStreams)
            {
                String[] indexProperties = queryGraph.getIndexProperties(pollingStream, historicalStreamNum);
                String[] keyProperties = queryGraph.getKeyProperties(pollingStream, historicalStreamNum);
                if (keyProperties == null)
                {
                    keyProperties = new String[0];
                    indexProperties = new String[0];
                }
                Class[] keyTypes = getPropertyTypes(typesPerStream[pollingStream], keyProperties);
                Class[] indexTypes = getPropertyTypes(typesPerStream[historicalStreamNum], indexProperties);

                HistoricalStreamIndexDesc desc = new HistoricalStreamIndexDesc(indexProperties, indexTypes, keyTypes);
                List<Integer> usedByStreams = indexesUsedByStreams.get(desc);
                if (usedByStreams == null)
                {
                    usedByStreams = new LinkedList<Integer>();
                    indexesUsedByStreams.put(desc, usedByStreams);
                }
                usedByStreams.add(pollingStream);
            }

            // There are multiple indexes required:
            // Build a master indexing strategy that forms multiple indexes and numbers each.
            if (indexesUsedByStreams.size() > 1)
            {
                final int numIndexes = indexesUsedByStreams.size();
                final PollResultIndexingStrategy[] indexingStrategies = new PollResultIndexingStrategy[numIndexes];

                // create an indexing strategy for each index
                int count = 0;
                for (Map.Entry<HistoricalStreamIndexDesc, List<Integer>> desc : indexesUsedByStreams.entrySet())
                {
                    int sampleStreamViewStreamNum = desc.getValue().get(0);
                    indexingStrategies[count] = JoinSetComposerFactoryImpl.determineIndexing(queryGraph, typesPerStream[historicalStreamNum], typesPerStream[sampleStreamViewStreamNum], historicalStreamNum, sampleStreamViewStreamNum).getSecond();
                    count++;
                }

                // create a master indexing strategy that utilizes each indexing strategy to create a set of indexes
                masterIndexingStrategy = new PollResultIndexingStrategy() {
                    public EventTable index(List<EventBean> pollResult, boolean isActiveCache)
                    {
                        EventTable[] tables = new EventTable[numIndexes];
                        for (int i = 0; i < numIndexes; i++)
                        {
                            tables[i] = indexingStrategies[i].index(pollResult, isActiveCache);
                        }
                        return new MultiIndexEventTable(tables);
                    }
                };
            }
        }

        // there is one type of index
        if (indexesUsedByStreams.size() == 1)
        {
            return JoinSetComposerFactoryImpl.determineIndexing(queryGraph, typesPerStream[historicalStreamNum], typesPerStream[streamViewStreamNum], historicalStreamNum, streamViewStreamNum);
        }

        // determine which index number the polling stream must use
        int indexUsed = 0;
        for (List<Integer> desc : indexesUsedByStreams.values())
        {
            if (desc.contains(streamViewStreamNum))
            {
                break;
            }
            indexUsed++;
        }

        // Use one of the indexes built by the master index and a lookup strategy
        final int indexNumber = indexUsed;
        final HistoricalIndexLookupStrategy innerLookupStrategy = JoinSetComposerFactoryImpl.determineIndexing(queryGraph, typesPerStream[historicalStreamNum], typesPerStream[streamViewStreamNum], historicalStreamNum, streamViewStreamNum).getFirst();
        HistoricalIndexLookupStrategy lookupStrategy = new HistoricalIndexLookupStrategy()
        {
            public Iterator<EventBean> lookup(EventBean lookupEvent, EventTable index)
            {
                MultiIndexEventTable multiIndex = (MultiIndexEventTable) index;
                EventTable indexToUse = multiIndex.getTables()[indexNumber];
                return innerLookupStrategy.lookup(lookupEvent, indexToUse);
            }
        };

        return new Pair<HistoricalIndexLookupStrategy, PollResultIndexingStrategy> (lookupStrategy, masterIndexingStrategy);
    }

    private Class[] getPropertyTypes(EventType eventType, String[] properties)
    {
        Class[] types = new Class[properties.length];
        for (int i = 0; i < properties.length; i++)
        {
            types[i] = JavaClassHelper.getBoxedType(eventType.getPropertyType(properties[i]));
        }
        return types;
    }
}
