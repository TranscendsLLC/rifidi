package com.espertech.esper.epl.expression;

import com.espertech.esper.type.RelationalOpEnum;
import com.espertech.esper.client.EventBean;

import java.util.Set;

/**
 * Strategy for subselects with ">/</<=/>= ANY".
 */
public class SubselectEvalStrategyRelOpAny implements SubselectEvalStrategy
{
    private final RelationalOpEnum.Computer computer;
    private final ExprNode valueExpr;
    private final ExprNode selectClauseExpr;
    private final ExprNode filterExpr;

    /**
     * Ctor.
     * @param computer operator
     * @param valueExpr LHS
     * @param selectClause select or null
     * @param filterExpr filter or null
     */
    public SubselectEvalStrategyRelOpAny(RelationalOpEnum.Computer computer, ExprNode valueExpr, ExprNode selectClause, ExprNode filterExpr)
    {
        this.computer = computer;
        this.valueExpr = valueExpr;
        this.selectClauseExpr = selectClause;
        this.filterExpr = filterExpr;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Set<EventBean> matchingEvents)
    {
        // Evaluate the value expression
        Object valueLeft = valueExpr.evaluate(eventsPerStream, isNewData);

        if (matchingEvents == null)
        {
            return false;
        }
        if (matchingEvents.size() == 0)
        {
            return false;
        }

        // Evaluation event-per-stream
        EventBean[] events = new EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);

        // Filter and check each row.
        boolean hasNonNullRow = false;
        boolean hasRows = false;
        for (EventBean subselectEvent : matchingEvents)
        {
            // Prepare filter expression event list
            events[0] = subselectEvent;

            // Eval filter expression
            if (filterExpr != null)
            {
                Boolean pass = (Boolean) filterExpr.evaluate(events, true);
                if ((pass == null) || (!pass))
                {
                    continue;
                }
            }
            hasRows = true;

            Object valueRight;
            if (selectClauseExpr != null)
            {
                valueRight = selectClauseExpr.evaluate(events, true);
            }
            else
            {
                valueRight = events[0].getUnderlying();
            }

            if (valueRight != null)
            {
                hasNonNullRow = true;
            }

            if ((valueLeft != null) && (valueRight != null))
            {
                if (computer.compare(valueLeft, valueRight))
                {
                    return true;
                }
            }
        }

        if (!hasRows)
        {
            return false;
        }
        if ((!hasNonNullRow) || (valueLeft == null))
        {
            return null;
        }
        return false;
    }
}
