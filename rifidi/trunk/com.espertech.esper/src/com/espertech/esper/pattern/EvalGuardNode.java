/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.pattern.guard.GuardFactory;
import com.espertech.esper.epl.spec.PatternGuardSpec;
import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a guard in the evaluation tree representing an event expressions.
 */
public final class EvalGuardNode extends EvalNode
{
    private PatternGuardSpec patternGuardSpec;
    private GuardFactory guardFactory;

    /**
     * Constructor.
     * @param patternGuardSpec - factory for guard construction
     */
    public EvalGuardNode(PatternGuardSpec patternGuardSpec)
    {
        this.patternGuardSpec = patternGuardSpec;
    }

    /**
     * Returns the guard object specification to use for instantiating the guard factory and guard.
     * @return guard specification
     */
    public PatternGuardSpec getPatternGuardSpec()
    {
        return patternGuardSpec;
    }

    /**
     * Supplies the guard factory to the node.
     * @param guardFactory is the guard factory
     */
    public void setGuardFactory(GuardFactory guardFactory)
    {
        this.guardFactory = guardFactory;
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

        return context.getPatternStateFactory().makeGuardState(parentNode, this, beginState, context, stateNodeId);
    }

    /**
     * Returns the guard factory.
     * @return guard factory
     */
    public GuardFactory getGuardFactory()
    {
        return guardFactory;
    }

    public final String toString()
    {
        return ("EvalGuardNode guardFactory=" + guardFactory +
                "  children=" + this.getChildNodes().size());
    }

    private static final Log log = LogFactory.getLog(EvalGuardNode.class);
}
