/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;

import java.util.Set;

/**
 * Strategy for subselects with "=/!=/<> ALL".
 */
public class SubselectEvalStrategyEqualsAll implements SubselectEvalStrategy
{
    private final boolean isNot;
    private final boolean mustCoerce;
    private final SimpleNumberCoercer coercer;
    private final ExprNode valueExpr;
    private final ExprNode filterExpr;
    private final ExprNode selectClauseExpr;

    /**
     * Ctor.
     * @param notIn false for =, true for !=
     * @param mustCoerce coercion required
     * @param coercionType type to coerce to
     * @param valueExpr LHS
     * @param selectClauseExpr select clause or null
     * @param filterExpr filter or null
     */
    public SubselectEvalStrategyEqualsAll(boolean notIn, boolean mustCoerce, Class coercionType, ExprNode valueExpr, ExprNode selectClauseExpr, ExprNode filterExpr)
    {
        isNot = notIn;
        this.mustCoerce = mustCoerce;
        if (mustCoerce)
        {
            coercer = SimpleNumberCoercerFactory.getCoercer(null, coercionType);
        }
        else
        {
            coercer = null;
        }
        this.valueExpr = valueExpr;
        this.filterExpr = filterExpr;
        this.selectClauseExpr = selectClauseExpr;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Set<EventBean> matchingEvents)
    {
        // Evaluate the child expression
        Object leftResult = valueExpr.evaluate(eventsPerStream, isNewData);

        if ((matchingEvents == null) || (matchingEvents.size() == 0))
        {
            return true;
        }

        // Evaluation event-per-stream
        EventBean[] events = new EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);

        if (isNot)
        {
            // Evaluate each select until we have a match
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (EventBean event : matchingEvents)
            {
                events[0] = event;

                // Eval filter expression
                if (filterExpr != null)
                {
                    Boolean pass = (Boolean) filterExpr.evaluate(events, true);
                    if ((pass == null) || (!pass))
                    {
                        continue;
                    }
                }
                if (leftResult == null)
                {
                    return null;
                }

                Object rightResult;
                if (selectClauseExpr != null)
                {
                    rightResult = selectClauseExpr.evaluate(events, true);
                }
                else
                {
                    rightResult = events[0].getUnderlying();
                }

                if (rightResult != null)
                {
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (leftResult.equals(rightResult))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (left.equals(right))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    hasNullRow = true;
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return true;
        }
        else
        {
            // Evaluate each select until we have a match
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (EventBean event : matchingEvents)
            {
                events[0] = event;

                // Eval filter expression
                if (filterExpr != null)
                {
                    Boolean pass = (Boolean) filterExpr.evaluate(events, true);
                    if ((pass == null) || (!pass))
                    {
                        continue;
                    }
                }
                if (leftResult == null)
                {
                    return null;
                }

                Object rightResult;
                if (selectClauseExpr != null)
                {
                    rightResult = selectClauseExpr.evaluate(events, true);
                }
                else
                {
                    rightResult = events[0].getUnderlying();
                }

                if (rightResult != null)
                {
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (!leftResult.equals(rightResult))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (!left.equals(right))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    hasNullRow = true;
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return true;
        }
    }
}