/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.LinkedList;

import com.espertech.esper.util.ExecutionPathDebugLog;

/**
 * This class contains the state of an 'every' operator in the evaluation state tree.
 * EVERY nodes work as a factory for new state subnodes. When a child node of an EVERY
 * node calls the evaluateTrue method on the EVERY node, the EVERY node will call newState on its child
 * node BEFORE it calls evaluateTrue on its parent node. It keeps a reference to the new child in
 * its list. (BEFORE because the root node could call quit on child nodes for stopping all
 * listeners).
 */
final class EvalEveryStateSpawnEvaluator implements Evaluator
{
    private boolean isEvaluatedTrue;

    public final boolean isEvaluatedTrue()
    {
        return isEvaluatedTrue;
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        log.warn("Event/request processing: Uncontrolled pattern matching of \"every\" operator - infinite loop when using EVERY operator on expression(s) containing a not operator");
        isEvaluatedTrue = true;
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        log.warn("Event/request processing: Uncontrolled pattern matching of \"every\" operator - infinite loop when using EVERY operator on expression(s) containing a not operator");
        isEvaluatedTrue = true;
    }

    private static final Log log = LogFactory.getLog(EvalEveryStateSpawnEvaluator.class);
}

/**
 * Contains the state collected by an "every" operator. The state includes handles to any sub-listeners
 * started by the operator.
 */
public final class EvalEveryStateNode extends EvalStateNode implements Evaluator
{
    private final List<EvalStateNode> spawnedNodes;
    private final MatchedEventMap beginState;
    private final PatternContext context;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param beginState contains the events that make up prior matches
     * @param context contains handles to services required
     * @param everyNode is the factory node associated to the state
     */
    public EvalEveryStateNode(Evaluator parentNode,
                                 EvalEveryNode everyNode,
                                  MatchedEventMap beginState,
                                  PatternContext context)
    {
        super(everyNode, parentNode, null);

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".constructor");
        }

        this.spawnedNodes = new LinkedList<EvalStateNode>();
        this.beginState = beginState.shallowCopy();
        this.context = context;

        EvalStateNode child = getFactoryNode().getChildNodes().get(0).newState(this, beginState, context, null);
        spawnedNodes.add(child);
    }

    public final void start()
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".start Starting single child");
        }

        if (spawnedNodes.size() != 1)
        {
            throw new IllegalStateException("EVERY state node is expected to have single child state node");
        }

        // During the start of the child we need to use the temporary evaluator to catch any event created during a start.
        // Events created during the start would likely come from the "not" operator.
        // Quit the new child again if
        EvalEveryStateSpawnEvaluator spawnEvaluator = new EvalEveryStateSpawnEvaluator();
        EvalStateNode child = spawnedNodes.get(0);
        child.setParentEvaluator(spawnEvaluator);
        child.start();

        // If the spawned expression turned true already, just quit it
        if (spawnEvaluator.isEvaluatedTrue())
        {
            child.quit();
        }
        else
        {
            child.setParentEvaluator(this);
        }
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".evaluateFalse");
        }

        fromNode.quit();
        spawnedNodes.remove(fromNode);

        // Spawn all nodes below this EVERY node
        // During the start of a child we need to use the temporary evaluator to catch any event created during a start
        // Such events can be raised when the "not" operator is used.
        EvalNode child = getFactoryNode().getChildNodes().get(0);
        EvalEveryStateSpawnEvaluator spawnEvaluator = new EvalEveryStateSpawnEvaluator();
        EvalStateNode spawned = child.newState(spawnEvaluator, beginState, context, null);
        spawned.start();

        // If the whole spawned expression already turned true, quit it again
        if (spawnEvaluator.isEvaluatedTrue())
        {
            spawned.quit();
        }
        else
        {
            spawnedNodes.add(spawned);
            spawned.setParentEvaluator(this);
        }
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".evaluateTrue fromNode=" + fromNode  + "  isQuitted=" + isQuitted);
        }

        if (isQuitted)
        {
            spawnedNodes.remove(fromNode);
        }

        // See explanation in EvalFilterStateNode for the type check
        if (fromNode instanceof EvalFilterStateNode)
        {
            // We do not need to newState new listeners here, since the filter state node below this node did not quit
        }
        else
        {
            // Spawn all nodes below this EVERY node
            // During the start of a child we need to use the temporary evaluator to catch any event created during a start
            // Such events can be raised when the "not" operator is used.
            EvalNode child = getFactoryNode().getChildNodes().get(0);
            EvalEveryStateSpawnEvaluator spawnEvaluator = new EvalEveryStateSpawnEvaluator();
            EvalStateNode spawned = child.newState(spawnEvaluator, beginState, context, null);
            spawned.start();

            // If the whole spawned expression already turned true, quit it again
            if (spawnEvaluator.isEvaluatedTrue())
            {
                spawned.quit();
            }
            else
            {
                spawnedNodes.add(spawned);
                spawned.setParentEvaluator(this);
            }
        }

        // All nodes indicate to their parents that their child node did not quit, therefore a false for isQuitted
        this.getParentEvaluator().evaluateTrue(matchEvent, this, false);
    }

    public final void quit()
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".quit Quitting EVERY-node all children");
        }

        // Stop all child nodes
        for (EvalStateNode child : spawnedNodes)
        {
            child.quit();
        }
    }

    public final Object accept(EvalStateNodeVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public final Object childrenAccept(EvalStateNodeVisitor visitor, Object data)
    {
        for (EvalStateNode spawnedNode : spawnedNodes)
        {
            spawnedNode.accept(visitor, data);
        }

        return data;
    }

    public final String toString()
    {
        return "EvalEveryStateNode spawnedChildren=" + spawnedNodes.size();
    }

    private static final Log log = LogFactory.getLog(EvalEveryStateNode.class);
}
