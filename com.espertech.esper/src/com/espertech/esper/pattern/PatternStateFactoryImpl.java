/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

/**
 * Default pattern state factory.
 */
public class PatternStateFactoryImpl implements PatternStateFactory
{
    private PatternContext context;

    public void setContext(PatternContext context)
    {
        this.context = context;
    }

    public EvalStateNode makeGuardState(Evaluator parentNode, EvalGuardNode evalGuardNode, MatchedEventMap beginState, PatternContext context, Object stateNodeId)
    {
        return new EvalGuardStateNode(parentNode, evalGuardNode, beginState, context, stateNodeId);
    }

    public EvalStateNode makeOrState(Evaluator parentNode, EvalOrNode evalOrNode, MatchedEventMap beginState, PatternContext context, Object stateNodeId)
    {
        return new EvalOrStateNode(parentNode, evalOrNode, beginState, context);
    }

    public EvalStateNode makeEveryStateNode(Evaluator parentNode, EvalEveryNode evalEveryNode, MatchedEventMap beginState, PatternContext context, Object stateNodeId)
    {
        return new EvalEveryStateNode(parentNode, evalEveryNode, beginState, context);
    }

    public EvalStateNode makeNotNode(Evaluator parentNode, EvalNotNode evalNotNode, MatchedEventMap beginState, Object stateNodeId)
    {
        return new EvalNotStateNode(parentNode, evalNotNode, beginState, context);
    }

    public EvalStateNode makeAndStateNode(Evaluator parentNode, EvalAndNode evalAndNode, MatchedEventMap beginState, Object stateNodeId)
    {
        return new EvalAndStateNode(parentNode, evalAndNode, beginState, context);
    }

    public EvalStateNode makeRootNode(EvalNode evalChildNode, MatchedEventMap beginState)
    {
        return new EvalRootStateNode(evalChildNode, beginState, context);
    }

    public EvalStateNode makeObserverNode(Evaluator parentNode, EvalObserverNode evalObserverNode, MatchedEventMap beginState, Object stateNodeId)
    {
        return new EvalObserverStateNode(parentNode, evalObserverNode, beginState, context);
    }

    public EvalStateNode makeFollowedByState(Evaluator parentNode, EvalFollowedByNode evalFollowedByNode, MatchedEventMap beginState, Object stateNodeId)
    {
        return new EvalFollowedByStateNode(parentNode, evalFollowedByNode, beginState, context);
    }

    public EvalStateNode makeMatchUntilState(Evaluator parentNode, EvalMatchUntilNode evalMatchUntilNode, MatchedEventMap beginState, Object stateNodeId)
    {
        return new EvalMatchUntilStateNode(parentNode, evalMatchUntilNode, beginState, context);
    }

    public EvalStateNode makeFilterStateNode(Evaluator parentNode, EvalFilterNode evalFilterNode, MatchedEventMap beginState, Object stateNodeId)
    {
        return new EvalFilterStateNode(parentNode, evalFilterNode, beginState, context);
    }

    public EvalStateNode makeStateNode(EvalNodeNumber evalNodeNumber, MatchedEventMap matchEvents, Object stateObjectId)
    {
        throw new UnsupportedOperationException("State node factory not supported");
    }

    public EvalStateNode makeParentStateNode(EvalNode evalNode, MatchedEventMap matchEvents, Object stateObjectId)
    {
        throw new UnsupportedOperationException("State node factory not supported");
    }
}
