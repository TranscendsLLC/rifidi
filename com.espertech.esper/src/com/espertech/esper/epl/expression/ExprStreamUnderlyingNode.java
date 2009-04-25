/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents an stream selector that returns the streams underlying event, or null if undefined.
 */
public class ExprStreamUnderlyingNode extends ExprNode
{
    private final String streamName;
    private int streamNum = -1;
    private Class type;

    /**
     * Ctor.
     * @param streamName is the name of the stream for which to return the underlying event
     */
    public ExprStreamUnderlyingNode(String streamName)
    {
        if (streamName == null)
        {
            throw new IllegalArgumentException("Stream name is null");
        }
        this.streamName = streamName;
    }

    /**
     * Returns the stream name.
     * @return stream name
     */
    public String getStreamName()
    {
        return streamName;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        String[] streams = streamTypeService.getStreamNames();
        for (int i = 0; i < streams.length; i++)
        {
            if ((streams[i] != null) && (streams[i].equals(streamName)))
            {
                streamNum = i;
                break;
            }
        }

        if (streamNum == -1)
        {
            throw new ExprValidationException("Stream by name '" + streamName + "' could not be found among all streams");
        }

        EventType eventType = streamTypeService.getEventTypes()[streamNum];
        type = eventType.getUnderlyingType();
    }

    public Class getType()
    {
        if (streamNum == -1)
        {
            throw new IllegalStateException("Stream underlying node has not been validated");
        }
        return type;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    /**
     * Returns stream id supplying the property value.
     * @return stream number
     */
    public int getStreamId()
    {
        if (streamNum == -1)
        {
            throw new IllegalStateException("Stream underlying node has not been validated");
        }
        return streamNum;
    }

    public String toString()
    {
        return "streamName=" + streamName +
                " streamNum=" + streamNum;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        EventBean event = eventsPerStream[streamNum];
        if (event == null)
        {
            return null;
        }
        return event.getUnderlying();
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(streamName);
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprStreamUnderlyingNode))
        {
            return false;
        }

        ExprStreamUnderlyingNode other = (ExprStreamUnderlyingNode) node;

        return (this.streamName.equals(other.streamName));
    }
}
