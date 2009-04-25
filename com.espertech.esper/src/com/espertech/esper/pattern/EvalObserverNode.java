/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.pattern.observer.ObserverFactory;
import com.espertech.esper.epl.spec.PatternObserverSpec;
import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class represents an observer expression in the evaluation tree representing an pattern expression.
 */
public final class EvalObserverNode extends EvalNode
{
    private final PatternObserverSpec patternObserverSpec;
    private ObserverFactory observerFactory;

    /**
     * Constructor.
     * @param patternObserverSpec is the factory to use to get an observer instance
     */
    public EvalObserverNode(PatternObserverSpec patternObserverSpec)
    {
        this.patternObserverSpec = patternObserverSpec;
    }

    /**
     * Returns the observer object specification to use for instantiating the observer factory and observer.
     * @return observer specification
     */
    public PatternObserverSpec getPatternObserverSpec()
    {
        return patternObserverSpec;
    }

    /**
     * Supplies the observer factory to the node.
     * @param observerFactory is the observer factory
     */
    public void setObserverFactory(ObserverFactory observerFactory)
    {
        this.observerFactory = observerFactory;
    }

    /**
     * Returns the observer factory.
     * @return factory for observer instances
     */
    public ObserverFactory getObserverFactory()
    {
        return observerFactory;
    }

    public final EvalStateNode newState(Evaluator parentNode,
                                        MatchedEventMap beginState,
                                        PatternContext context,
                                        Object stateNodeId)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".newState");
        }

        if (!getChildNodes().isEmpty())
        {
            throw new IllegalStateException("Expected number of child nodes incorrect, expected no child nodes, found "
                    + getChildNodes().size());
        }

        return context.getPatternStateFactory().makeObserverNode(parentNode, this, beginState, stateNodeId);
    }

    public final String toString()
    {
        return ("EvalObserverNode observerFactory=" + observerFactory +
                "  children=" + this.getChildNodes().size());
    }

    private static final Log log = LogFactory.getLog(EvalObserverNode.class);
}
