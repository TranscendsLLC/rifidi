/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * This class represents a match-until observer in the evaluation tree representing any event expressions.
 */
public final class EvalMatchUntilNode extends EvalNode
{
    private final EvalMatchUntilSpec spec;
    private String[] tagsArrayed;

    /**
     * Ctor.
     * @param spec specifies an optional range
     */
    public EvalMatchUntilNode(EvalMatchUntilSpec spec)
    {
        this.spec = spec;
    }

    /**
     * Returns the range specification, which is never null however may contain null low and high endpoints.
     * @return range spec
     */
    public EvalMatchUntilSpec getSpec()
    {
        return spec;
    }

    /**
     * Returns an array of tags for events, which is all tags used within the repeat-operator.
     * @return array of tags
     */
    public String[] getTagsArrayed()
    {
        return tagsArrayed;
    }

    /**
     * Sets the tags used within the repeat operator.
     * @param tagsArrayedSet tags used within the repeat operator
     */
    public void setTagsArrayedSet(Set<String> tagsArrayedSet)
    {
        if (tagsArrayedSet != null)
        {
            tagsArrayed = tagsArrayedSet.toArray(new String[tagsArrayedSet.size()]);
        }
        else
        {
            tagsArrayed = new String[0];
        }
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

        // if the high and low are bounded to the same value, there should be no until
        if ((spec.getLowerBounds() != null) && (spec.getLowerBounds().equals(spec.getUpperBounds())))
        {
            if (getChildNodes().size() > 2)
            {
                throw new IllegalStateException("Expected number of child nodes incorrect, expected 1 (no-until) or 2 (with until) child nodes, found "
                        + getChildNodes().size() + " for bound match");
            }
        }
        else
        {
            // expecting a match-expression and an until-expression
            if (getChildNodes().size() != 2)
            {
                throw new IllegalStateException("Expected number of child nodes incorrect, expected 2 child nodes, found "
                        + getChildNodes().size());
            }
        }

        return context.getPatternStateFactory().makeMatchUntilState(parentNode, this, beginState, stateNodeId);
    }

    public final String toString()
    {
        return ("EvalMatchUntilNode children=" + this.getChildNodes().size());
    }

    private static final Log log = LogFactory.getLog(EvalMatchUntilNode.class);
}
