/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.plan;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.espertech.esper.epl.join.exec.ExecNode;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.HistoricalStreamIndexList;
import com.espertech.esper.client.EventType;
import com.espertech.esper.util.IndentWriter;
import com.espertech.esper.view.Viewable;

/**
 * Specification node for a query execution plan to be extended by specific execution specification nodes.
 */
public abstract class QueryPlanNode
{
    /**
     * Make execution node from this specification.
     * @param indexesPerStream - tables build for each stream
     * @param streamTypes - event type of each stream
     * @param streamViews - viewable per stream for access to historical data
     * @param historicalStreamIndexLists index management for historical streams
     * @return execution node matching spec
     */
    public abstract ExecNode makeExec(EventTable[][] indexesPerStream, EventType[] streamTypes, Viewable[] streamViews, HistoricalStreamIndexList[] historicalStreamIndexLists);

    /**
     * Print a long readable format of the query node to the supplied PrintWriter.
     * @param writer is the indentation writer to print to
     */
    protected abstract void print(IndentWriter writer);

    /**
     * Print in readable format the execution plan spec.
     * @param planNodeSpecs - plans to print
     * @return readable text with plans
     */
    @SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "StringContatenationInLoop"})
    public static String print(QueryPlanNode[] planNodeSpecs)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("QueryPlanNode[]\n");

        for (int i = 0; i < planNodeSpecs.length; i++)
        {
            buffer.append("  node spec " + i + " :\n");

            StringWriter buf = new StringWriter();
            PrintWriter printer = new PrintWriter(buf);
            IndentWriter indentWriter = new IndentWriter(printer, 4, 2);

            if (planNodeSpecs[i] != null)
            {
                planNodeSpecs[i].print(indentWriter);
            }
            else
            {
                indentWriter.println("no plan (historical)");
            }

            buffer.append(buf.toString());
        }

        return buffer.toString();
    }
}
