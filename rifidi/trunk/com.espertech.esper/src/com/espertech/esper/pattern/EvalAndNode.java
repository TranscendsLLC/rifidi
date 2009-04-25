/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import com.espertech.esper.util.ExecutionPathDebugLog;


/**
 * This class represents an 'and' operator in the evaluation tree representing an event expressions.
 */
public final class EvalAndNode extends EvalNode
{
    public final EvalStateNode newState(Evaluator parentNode,
                                        MatchedEventMap beginState,
                                        PatternContext context,
                                        Object stateNodeId)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".newState");
        }

        if (getChildNodes().size() <= 1)
        {
            throw new IllegalStateException("Expected number of child nodes incorrect, expected >=2 nodes, found "
                    + getChildNodes().size());
        }

        return context.getPatternStateFactory().makeAndStateNode(parentNode, this, beginState, stateNodeId);
    }

    public final String toString()
    {
        return ("EvalAndNode children=" + this.getChildNodes().size());
    }

    private static final Log log = LogFactory.getLog(EvalAndNode.class);
}
