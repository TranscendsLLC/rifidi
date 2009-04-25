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
import com.espertech.esper.util.ExecutionPathDebugLog;

/**
 * This class is always the root node in the evaluation tree representing an event expression.
 * It hold the handle to the EPStatement implementation for notifying when matches are found.
 */
public final class EvalRootNode extends EvalNode implements PatternStarter
{
    public final PatternStopCallback start(PatternMatchCallback callback,
                                           PatternContext context)
    {
        MatchedEventMap beginState = new MatchedEventMapImpl();
        EvalStateNode rootStateNode = newState(null, beginState, context, null);
        EvalRootState rootState = (EvalRootState) rootStateNode;
        rootState.setCallback(callback);
        rootStateNode.start();
        return rootState;
    }

    public final EvalStateNode newState(Evaluator parentNode,
                                        MatchedEventMap beginState,
                                        PatternContext context, Object stateNodeId)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".newState");
        }

        if (getChildNodes().size() != 1)
        {
            throw new IllegalStateException("Expected number of child nodes incorrect, expected 1 child node, found "
                    + getChildNodes().size());
        }

        return context.getPatternStateFactory().makeRootNode(this.getChildNodes().get(0), beginState);
    }

    public final String toString()
    {
        return ("EvalRootNode children=" + this.getChildNodes().size());
    }

    private static final Log log = LogFactory.getLog(EvalRootNode.class);
}
