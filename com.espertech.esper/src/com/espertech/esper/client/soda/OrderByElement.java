/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.io.Serializable;
import java.io.StringWriter;

/**
 * A single entry in an order-by clause consisting of an expression and order ascending or descending flag.
 */
public class OrderByElement implements Serializable
{
    private static final long serialVersionUID = 0L;

    private Expression expression;
    private boolean isDescending;

    /**
     * Ctor.
     * @param expression is the expression to order by
     * @param descending true for descending sort, false for ascending sort
     */
    public OrderByElement(Expression expression, boolean descending)
    {
        this.expression = expression;
        isDescending = descending;
    }

    /**
     * Returns the order-by value expression.
     * @return expression
     */
    public Expression getExpression()
    {
        return expression;
    }

    /**
     * Sets the order-by value expression.
     * @param expression provides order-by values
     */
    public void setExpression(Expression expression)
    {
        this.expression = expression;
    }

    /**
     * Returns true for descending sorts for this column, false for ascending sort.
     * @return true for descending sort
     */
    public boolean isDescending()
    {
        return isDescending;
    }

    /**
     * Set to true for descending sorts for this column, false for ascending sort.
     * @param descending true for descending sort
     */
    public void setDescending(boolean descending)
    {
        isDescending = descending;
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPL(StringWriter writer)
    {
        expression.toEPL(writer);
        if (isDescending)
        {
            writer.write(" desc");
        }
    }
}
