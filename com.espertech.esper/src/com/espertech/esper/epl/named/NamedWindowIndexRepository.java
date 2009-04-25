/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyIndTableCoerceAdd;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;
import com.espertech.esper.epl.lookup.JoinedPropDesc;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.collection.MultiKey;

import java.util.*;

/**
 * A repository of index tables for use with a single named window and all it's deleting statements that
 * may use the indexes to correlate triggering events with indexed events of the named window.
 * <p>
 * Maintains index tables and keeps a reference count for user. Allows reuse of indexes for multiple
 * deleting statements.
 */
public class NamedWindowIndexRepository
{
    private List<EventTable> tables;
    private Map<MultiKey<JoinedPropDesc>, Pair<PropertyIndexedEventTable, Integer>> tableIndexes;

    /**
     * Ctor.
     */
    public NamedWindowIndexRepository()
    {
        tables = new ArrayList<EventTable>();
        tableIndexes = new HashMap<MultiKey<JoinedPropDesc>, Pair<PropertyIndexedEventTable, Integer>>();
    }

    /**
     * Create a new index table or use an existing index table, by matching the
     * join descriptor properties to an existing table.
     * @param joinedPropDesc must be in sorted natural order and define the properties joined
     * @param prefilledEvents is the events to enter into a new table, if a new table is created
     * @param indexedType is the type of event to hold in the index
     * @param mustCoerce is an indicator whether coercion is required or not.
     * @return new or existing index table
     */
    public PropertyIndexedEventTable addTable(JoinedPropDesc[] joinedPropDesc,
                               Iterable<EventBean> prefilledEvents,
                               EventType indexedType,
                               boolean mustCoerce)
    {
        MultiKey<JoinedPropDesc> indexPropKey = new MultiKey<JoinedPropDesc>(joinedPropDesc);

        // Get an existing table, if any
        Pair<PropertyIndexedEventTable, Integer> refTablePair = tableIndexes.get(indexPropKey);
        if (refTablePair != null)
        {
            refTablePair.setSecond(refTablePair.getSecond() + 1);
            return refTablePair.getFirst();
        }

        String[] indexProps = JoinedPropDesc.getIndexProperties(joinedPropDesc);
        Class[] coercionTypes = JoinedPropDesc.getCoercionTypes(joinedPropDesc);
        PropertyIndexedEventTable table;
        if (!mustCoerce)
        {
            table = new PropertyIndexedEventTable(0, indexedType, indexProps);
        }
        else
        {
            table = new PropertyIndTableCoerceAdd(0, indexedType, indexProps, coercionTypes);
        }

        // fill table since its new
        EventBean[] events = new EventBean[1];
        for (EventBean prefilledEvent : prefilledEvents)
        {
            events[0] = prefilledEvent;
            table.add(events);
        }

        // add table
        tables.add(table);

        // add index, reference counted
        tableIndexes.put(indexPropKey, new Pair<PropertyIndexedEventTable, Integer>(table, 1));

        return table;
    }

    /**
     * Remove a reference to an index table, decreasing its reference count.
     * If the table is no longer used, discard it and no longer update events into the index.
     * @param table to remove a reference to
     */
    public void removeTableReference(EventTable table)
    {
        for (Map.Entry<MultiKey<JoinedPropDesc>, Pair<PropertyIndexedEventTable, Integer>> entry : tableIndexes.entrySet())
        {
            if (entry.getValue().getFirst() == table)
            {
                int current = entry.getValue().getSecond();
                if (current > 1)
                {
                    current--;
                    entry.getValue().setSecond(current);
                    break;
                }

                tables.remove(table);
                tableIndexes.remove(entry.getKey());
                break;
            }
        }
    }

    /**
     * Returns a list of current index tables in the repository.
     * @return index tables
     */
    public List<EventTable> getTables()
    {
        return tables;
    }

    /**
     * Destroy indexes.
     */
    public void destroy()
    {
        tables.clear();
        tableIndexes.clear();
    }
}
