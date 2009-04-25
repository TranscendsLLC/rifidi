/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.plan;

import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.join.HistoricalIndexLookupStrategy;
import com.espertech.esper.epl.join.PollResultIndexingStrategy;
import com.espertech.esper.epl.join.exec.ExecNode;
import com.espertech.esper.epl.join.exec.HistoricalDataExecNode;
import com.espertech.esper.epl.join.exec.HistoricalTableLookupStrategy;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.HistoricalStreamIndexList;
import com.espertech.esper.client.EventType;
import com.espertech.esper.util.IndentWriter;
import com.espertech.esper.view.HistoricalEventViewable;
import com.espertech.esper.view.Viewable;

/**
 * Query plan for performing a historical data lookup.
 * <p>
 * Translates into a particular execution for use in regular and outer joins.
 */
public class HistoricalDataPlanNode extends QueryPlanNode
{
    private final int streamNum;
    private final int rootStreamNum;
    private final int lookupStreamNum;
    private final int numStreams;
    private final ExprNode outerJoinExprNode;

    /**
     * Ctor.
     * @param streamNum the historical stream num
     * @param rootStreamNum the stream number of the query plan providing incoming events
     * @param lookupStreamNum the stream that provides polling/lookup events
     * @param numStreams number of streams in join
     * @param exprNode outer join expression node or null if none defined
     */
    public HistoricalDataPlanNode(int streamNum, int rootStreamNum, int lookupStreamNum, int numStreams, ExprNode exprNode)
    {
        this.streamNum = streamNum;
        this.rootStreamNum = rootStreamNum;
        this.lookupStreamNum = lookupStreamNum;
        this.numStreams = numStreams;
        this.outerJoinExprNode = exprNode;
    }

    public ExecNode makeExec(EventTable[][] indexesPerStream, EventType[] streamTypes, Viewable[] streamViews, HistoricalStreamIndexList[] historicalStreamIndexLists)
    {
        Pair<HistoricalIndexLookupStrategy, PollResultIndexingStrategy> pair = historicalStreamIndexLists[streamNum].getStrategy(lookupStreamNum);
        HistoricalEventViewable viewable = (HistoricalEventViewable) streamViews[streamNum];
        return new HistoricalDataExecNode(viewable, pair.getSecond(), pair.getFirst(), numStreams, streamNum);
    }

    /**
     * Returns the table lookup strategy for use in outer joins.
     * @param streamViews all views in join
     * @param pollingStreamNum the stream number of the stream looking up into the historical
     * @param historicalStreamIndexLists the index management for the historical stream
     * @return strategy
     */
    public HistoricalTableLookupStrategy makeOuterJoinStategy(Viewable[] streamViews, int pollingStreamNum, HistoricalStreamIndexList[] historicalStreamIndexLists)
    {
        Pair<HistoricalIndexLookupStrategy, PollResultIndexingStrategy> pair = historicalStreamIndexLists[streamNum].getStrategy(pollingStreamNum);
        HistoricalEventViewable viewable = (HistoricalEventViewable) streamViews[streamNum];
        return new HistoricalTableLookupStrategy(viewable, pair.getSecond(), pair.getFirst(), numStreams, streamNum, rootStreamNum, outerJoinExprNode);
    }

    protected void print(IndentWriter writer)
    {
        writer.incrIndent();
        writer.println("HistoricalDataPlanNode streamNum=" + streamNum);
    }
}
