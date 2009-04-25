/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.view.internal.BufferView;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.collection.MultiKey;

import java.util.Set;
import java.util.HashSet;

/**
 * Implements a method for pre-loading (initializing) join indexes from a filled buffer.
 */
public class JoinPreloadMethodImpl implements JoinPreloadMethod
{
    private final int numStreams;
    private final BufferView[] bufferViews;
    private final JoinSetComposer joinSetComposer;

    /**
     * Ctor.
     * @param numStreams number of streams
     * @param joinSetComposer the composer holding stream indexes
     */
    public JoinPreloadMethodImpl(int numStreams, JoinSetComposer joinSetComposer)
    {
        this.numStreams = numStreams;
        this.bufferViews = new BufferView[numStreams];
        this.joinSetComposer = joinSetComposer;
    }

    /**
     * Sets the buffer for a stream to preload events from.
     * @param view buffer
     * @param stream the stream number for the buffer
     */
    public void setBuffer(BufferView view, int stream)
    {
        bufferViews[stream] = view;
    }

    public void preloadFromBuffer(int stream)
    {
        EventBean[] preloadEvents = bufferViews[stream].getNewDataBuffer().getAndFlush();
        EventBean[][] eventsPerStream = new EventBean[numStreams][];
        eventsPerStream[stream] = preloadEvents;
        joinSetComposer.init(eventsPerStream);
    }

    public void preloadAggregation(ResultSetProcessor resultSetProcessor)
    {
        Set<MultiKey<EventBean>> newEvents = joinSetComposer.staticJoin();
        Set<MultiKey<EventBean>> oldEvents = new HashSet<MultiKey<EventBean>>();
        resultSetProcessor.processJoinResult(newEvents, oldEvents, false);
    }
}
