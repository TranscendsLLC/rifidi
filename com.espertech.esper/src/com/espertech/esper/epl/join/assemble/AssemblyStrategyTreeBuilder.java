/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.assemble;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.espertech.esper.util.IndentWriter;
import com.espertech.esper.epl.join.plan.NStreamOuterQueryPlanBuilder;

/**
 * Builds a tree of assembly nodes given a strategy for how to join streams.
 */
public class AssemblyStrategyTreeBuilder
{
    /**
     * Builds a tree of {@link BaseAssemblyNode} from join strategy information.
     * @param rootStream - the root stream supplying the event to evaluate
     * @param streamsJoinedPerStream - a map in which the key is the stream number to supply an event,
     * and the value is an array of streams to find events in for the given event
     * @param isRequiredPerStream - indicates which streams are required join streams versus optional streams
     * @return root assembly node
     */
    public static BaseAssemblyNode build(int rootStream, Map<Integer, int[]> streamsJoinedPerStream, boolean isRequiredPerStream[])
    {
        if (streamsJoinedPerStream.size() < 3)
        {
            throw new IllegalArgumentException("Not a 3-way join");
        }
        if ((rootStream < 0) || (rootStream >= streamsJoinedPerStream.size()))
        {
            throw new IllegalArgumentException("Invalid root stream");
        }
        if (isRequiredPerStream.length != streamsJoinedPerStream.size())
        {
            throw new IllegalArgumentException("Arrays not matching up");
        }

        NStreamOuterQueryPlanBuilder.verifyJoinedPerStream(rootStream, streamsJoinedPerStream);

        if (log.isDebugEnabled())
        {
            log.debug(".build Building node for root stream " + rootStream +
                            " streamsJoinedPerStream=" + NStreamOuterQueryPlanBuilder.print(streamsJoinedPerStream) +
                            " isRequiredPerStream=" + Arrays.toString(isRequiredPerStream));
        }

        BaseAssemblyNode topNode = createNode(true, rootStream, streamsJoinedPerStream.size(), streamsJoinedPerStream.get(rootStream), isRequiredPerStream);

        recursiveBuild(rootStream, topNode, streamsJoinedPerStream, isRequiredPerStream);

        if (log.isDebugEnabled())
        {
            StringWriter buf = new StringWriter();
            PrintWriter print = new PrintWriter(buf);
            IndentWriter indentWriter = new IndentWriter(print, 0, 2);
            topNode.printDescendends(indentWriter);

            log.debug(".build Dumping root node for stream " + rootStream + ": \n" + buf.toString());
        }

        return topNode;
    }

    private static void recursiveBuild(int parentStreamNum, BaseAssemblyNode parentNode,
                                Map<Integer, int[]> streamsJoinedPerStream, boolean isRequiredPerStream[])
    {
        int numStreams = streamsJoinedPerStream.size();

        for (int i = 0; i < streamsJoinedPerStream.get(parentStreamNum).length; i++)
        {
            int streamJoined = streamsJoinedPerStream.get(parentStreamNum)[i];
            BaseAssemblyNode childNode = createNode(false, streamJoined, numStreams, streamsJoinedPerStream.get(streamJoined), isRequiredPerStream);
            parentNode.addChild(childNode);

            if (streamsJoinedPerStream.get(streamJoined).length > 0)
            {
                recursiveBuild(streamJoined, childNode, streamsJoinedPerStream, isRequiredPerStream);
            }
        }
    }

    private static BaseAssemblyNode createNode(boolean isRoot, int streamNum, int numStreams, int[] joinedStreams, boolean isRequiredPerStream[])
    {
        if (joinedStreams.length == 0)
        {
            return new LeafAssemblyNode(streamNum, numStreams);
        }
        if (joinedStreams.length == 1)
        {
            int joinedStream = joinedStreams[0];
            boolean isRequired = isRequiredPerStream[joinedStream];
            if (isRequired)
            {
                if (isRoot)
                {
                    return new RootRequiredAssemblyNode(streamNum, numStreams);
                }
                else
                {
                    return new BranchRequiredAssemblyNode(streamNum, numStreams);
                }
            }
            else
            {
                if (isRoot)
                {
                    return new RootOptionalAssemblyNode(streamNum, numStreams);
                }
                else
                {
                    return new BranchOptionalAssemblyNode(streamNum, numStreams);
                }
            }
        }

        // Determine if all substream are outer (optional) joins
        boolean allSubStreamsOptional = true;
        for (int i = 0; i < joinedStreams.length; i++)
        {
            int stream = joinedStreams[i];
            if (isRequiredPerStream[stream])
            {
                allSubStreamsOptional = false;
            }
        }

        // Make node for building a cartesian product
        if (isRoot)
        {
            return new RootCartProdAssemblyNode(streamNum, numStreams, allSubStreamsOptional);
        }
        else
        {
            return new CartesianProdAssemblyNode(streamNum, numStreams, allSubStreamsOptional);
        }
    }

    private static final Log log = LogFactory.getLog(AssemblyStrategyTreeBuilder.class);
}
