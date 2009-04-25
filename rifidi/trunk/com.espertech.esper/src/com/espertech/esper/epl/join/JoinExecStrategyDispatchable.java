/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join;

import com.espertech.esper.collection.FlushedEventBuffer;
import com.espertech.esper.core.EPStatementDispatch;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.view.internal.BufferObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * This class reacts to any new data buffered by registring with the dispatch service.
 * When dispatched via execute, it takes the buffered events and hands these to the join execution strategy.
 */
public class JoinExecStrategyDispatchable implements EPStatementDispatch, BufferObserver
{
    private final JoinExecutionStrategy joinExecutionStrategy;
    private final Map<Integer, FlushedEventBuffer> oldStreamBuffer;
    private final Map<Integer, FlushedEventBuffer> newStreamBuffer;
    private final int numStreams;

    private boolean hasNewData;

    /**
     * CTor.
     * @param joinExecutionStrategy - strategy for executing the join
     * @param numStreams - number of stream
     */
    public JoinExecStrategyDispatchable(JoinExecutionStrategy joinExecutionStrategy, int numStreams)
    {
        this.joinExecutionStrategy = joinExecutionStrategy;
        this.numStreams = numStreams;

        oldStreamBuffer = new HashMap<Integer, FlushedEventBuffer>();
        newStreamBuffer = new HashMap<Integer, FlushedEventBuffer>();
    }

    public void execute()
    {
        if (!hasNewData)
        {
            return;
        }
        hasNewData = false;

        EventBean[][] oldDataPerStream = new EventBean[numStreams][];
        EventBean[][] newDataPerStream = new EventBean[numStreams][];

        for (int i = 0; i < numStreams; i++)
        {
            oldDataPerStream[i] = getBufferData(oldStreamBuffer.get(i));
            newDataPerStream[i] = getBufferData(newStreamBuffer.get(i));
        }

        joinExecutionStrategy.join(newDataPerStream, oldDataPerStream);
    }

    private static EventBean[] getBufferData(FlushedEventBuffer buffer)
    {
        if (buffer == null)
        {
            return null;
        }
        return buffer.getAndFlush();
    }

    public void newData(int streamId, FlushedEventBuffer newEventBuffer, FlushedEventBuffer oldEventBuffer)
    {
        hasNewData = true;
        newStreamBuffer.put(streamId, newEventBuffer);
        oldStreamBuffer.put(streamId, oldEventBuffer);
    }
}
