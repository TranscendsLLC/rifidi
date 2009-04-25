/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;


import java.util.List;
import java.util.LinkedList;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import com.espertech.esper.util.ExecutionPathDebugLog;

/**
 * This class represents the state of a "or" operator in the evaluation state tree.
 */
public final class EvalOrStateNode extends EvalStateNode implements Evaluator
{
    private final List<EvalStateNode> childNodes;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param beginState contains the events that make up prior matches
     * @param context contains handles to services required
     * @param evalOrNode is the factory node associated to the state
     */
    public EvalOrStateNode(Evaluator parentNode,
                                 EvalOrNode evalOrNode,
                                 MatchedEventMap beginState,
                                 PatternContext context)
    {
        super(evalOrNode, parentNode, null);

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".constructor");
        }

        this.childNodes = new LinkedList<EvalStateNode>();

        // In an "or" expression we need to create states for all child expressions/listeners,
        // since all are going to be started
        for (EvalNode node : getFactoryNode().getChildNodes())
        {
            EvalStateNode childState = node.newState(this, beginState, context, null);
            childNodes.add(childState);
        }
    }

    public final void start()
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".start Starting or-expression all children, size=" + getFactoryNode().getChildNodes().size());
        }

        if (childNodes.size() != getFactoryNode().getChildNodes().size())
        {
            throw new IllegalStateException("OR state node does not have the required child state nodes");
        }

        // In an "or" expression we start all child listeners
        for (EvalStateNode child : childNodes)
        {
            child.start();
        }
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".evaluateTrue fromNode=" + fromNode.hashCode());
        }

        // If one of the children quits, the whole or expression turns true and all subexpressions must quit
        if (isQuitted)
        {
            childNodes.remove(fromNode);
            quit();     // Quit the remaining listeners
        }

        this.getParentEvaluator().evaluateTrue(matchEvent, this, isQuitted);
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".evaluateFalse fromNode=" + fromNode.hashCode());
        }
    }

    public final void quit()
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".quit Stopping all children");
        }

        for (EvalStateNode child : childNodes)
        {
            child.quit();
        }
        childNodes.clear();
    }

    public final Object accept(EvalStateNodeVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public final Object childrenAccept(EvalStateNodeVisitor visitor, Object data)
    {
        for (EvalStateNode node : childNodes)
        {
            node.accept(visitor, data);
        }
        return data;
    }

    public final String toString()
    {
        return "EvalOrStateNode nodes=" + childNodes.size();
    }

    private static final Log log = LogFactory.getLog(EvalOrStateNode.class);
}
