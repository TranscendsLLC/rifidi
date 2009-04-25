/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.exec;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.join.rep.Cursor;
import com.espertech.esper.epl.join.rep.Node;
import com.espertech.esper.epl.join.PollResultIndexingStrategy;
import com.espertech.esper.epl.join.HistoricalIndexLookupStrategy;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.view.HistoricalEventViewable;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A lookup strategy for use in outer joins onto historical streams.
 */
public class HistoricalTableLookupStrategy implements TableLookupStrategy
{
    private final HistoricalEventViewable viewable;
    private final PollResultIndexingStrategy indexingStrategy;
    private final HistoricalIndexLookupStrategy lookupStrategy;
    private final int streamNum;
    private final int rootStreamNum;
    private final ExprNode outerJoinExprNode;
    private final EventBean[][] lookupEventsPerStream;

    /**
     * Ctor.
     * @param viewable providing the polling access
     * @param indexingStrategy strategy for indexing results
     * @param lookupStrategy strategy for using indexed results
     * @param numStreams number of streams
     * @param streamNum stream number of the historical stream
     * @param rootStreamNum the query plan root stream number
     * @param outerJoinExprNode an optional outer join expression
     */
    public HistoricalTableLookupStrategy(HistoricalEventViewable viewable, PollResultIndexingStrategy indexingStrategy, HistoricalIndexLookupStrategy lookupStrategy, int numStreams, int streamNum, int rootStreamNum, ExprNode outerJoinExprNode)
    {
        this.viewable = viewable;
        this.indexingStrategy = indexingStrategy;
        this.lookupStrategy = lookupStrategy;
        this.streamNum = streamNum;
        this.rootStreamNum = rootStreamNum;
        this.outerJoinExprNode = outerJoinExprNode;
        lookupEventsPerStream = new EventBean[1][numStreams];
    }

    public Set<EventBean> lookup(EventBean event, Cursor cursor)
    {
        int currStream = cursor.getStream();

        // fill the current stream and the deep cursor events
        lookupEventsPerStream[0][currStream] = event;
        recursiveFill(lookupEventsPerStream[0], cursor.getNode());

        // poll
        EventTable[] indexPerLookupRow = viewable.poll(lookupEventsPerStream, indexingStrategy);

        Set<EventBean> result = null;
        for (EventTable index : indexPerLookupRow)
        {
            // Using the index, determine a subset of the whole indexed table to process, unless
            // the strategy is a full table scan
            Iterator<EventBean> subsetIter = lookupStrategy.lookup(event, index);

            if (subsetIter != null)
            {
                // Add each row to the join result or, for outer joins, run through the outer join filter
                for (;subsetIter.hasNext();)
                {
                    EventBean candidate = subsetIter.next();

                    lookupEventsPerStream[0][streamNum] = candidate;
                    Boolean pass = (Boolean) outerJoinExprNode.evaluate(lookupEventsPerStream[0], true);
                    if ((pass != null) && (pass))
                    {
                        if (result == null)
                        {
                            result = new HashSet<EventBean>();
                        }
                        result.add(candidate);
                    }
                }
            }
        }

        return result;
    }

    private void recursiveFill(EventBean[] lookupEventsPerStream, Node node)
    {
        if (node == null)
        {
            return;
        }

        Node parent = node.getParent();
        if (parent == null)
        {
            lookupEventsPerStream[rootStreamNum] = node.getParentEvent();
            return;
        }

        lookupEventsPerStream[parent.getStream()] = parent.getParentEvent();
        recursiveFill(lookupEventsPerStream, parent);
    }
}
