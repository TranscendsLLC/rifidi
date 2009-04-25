package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;

import java.util.Set;

/**
 * Strategy for subselects with "=/!=/<> ANY".
 */
public class SubselectEvalStrategyEqualsAny implements SubselectEvalStrategy
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
    public SubselectEvalStrategyEqualsAny(boolean notIn, boolean mustCoerce, Class coercionType, ExprNode valueExpr, ExprNode selectClauseExpr, ExprNode filterExpr)
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
        if ((matchingEvents == null) || (matchingEvents.size() == 0))
        {
            return false;
        }

        // Evaluate the child expression
        Object leftResult = valueExpr.evaluate(eventsPerStream, isNewData);

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

                Object rightResult;
                if (selectClauseExpr != null)
                {
                    rightResult = selectClauseExpr.evaluate(events, true);
                }
                else
                {
                    rightResult = events[0].getUnderlying();
                }

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

                if (rightResult != null)
                {
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (!leftResult.equals(rightResult))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (!left.equals(right))
                        {
                            return true;
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

            return false;
        }
        else
        {
            // Evaluate each select until we have a match
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (EventBean event : matchingEvents)
            {
                events[0] = event;

                Object rightResult;
                if (selectClauseExpr != null)
                {
                    rightResult = selectClauseExpr.evaluate(events, true);
                }
                else
                {
                    rightResult = events[0].getUnderlying();
                }

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

                if (rightResult != null)
                {
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (leftResult.equals(rightResult))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (left.equals(right))
                        {
                            return true;
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

            return false;
        }
    }
}
