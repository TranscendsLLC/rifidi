package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * For use in view parameter lists, this is a wrapper expression
 * that adds an ascending or descending sort indicator to its single child expression.
 */
public class OrderedObjectParamExpression extends ExpressionBase
{
    private boolean isDescending;

    /**
     * Ctor.
     * @param descending to indicate a descending sort, or false for ascending
     */
    public OrderedObjectParamExpression(boolean descending)
    {
        isDescending = descending;
    }

    /**
     * Returns true for descending, false for ascending.
     * @return indicator for descending sort
     */
    public boolean isDescending()
    {
        return isDescending;
    }

    public void toEPL(StringWriter writer)
    {
        this.getChildren().get(0).toEPL(writer);
        if (isDescending)
        {
            writer.write(" desc");
        }
        else
        {
            writer.write(" asc");
        }
    }
}
