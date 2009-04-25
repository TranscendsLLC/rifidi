package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.client.EventBean;

/**
 * A placeholder expression for view/pattern object parameters that allow
 * sorting expression values ascending or descending.
 */
public class ExprOrderedExpr extends ExprNode
{
    private final boolean isDescending;

    /**
     * Ctor.
     * @param descending is true for descending sorts
     */
    public ExprOrderedExpr(boolean descending)
    {
        isDescending = descending;
    }

    public String toExpressionString()
    {
        String inner = this.getChildNodes().get(0).toExpressionString();
        if (isDescending)
        {
            return inner + " desc";
        }
        return inner;
    }

    public boolean isConstantResult()
    {
        return getChildNodes().get(0).isConstantResult();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprOrderedExpr))
        {
            return false;
        }
        ExprOrderedExpr other = (ExprOrderedExpr) node;
        return other.isDescending == this.isDescending;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        // always valid
    }

    public Class getType()
    {
        return getChildNodes().get(0).getType();
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        return getChildNodes().get(0).evaluate(eventsPerStream, isNewData);
    }

    /**
     * Returns true for descending sort.
     * @return indicator for ascending or descending sort
     */
    public boolean isDescending()
    {
        return isDescending;
    }
}
