/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * In-expression for a set of values returned by a lookup.
 */
public class SubqueryInExpression extends ExpressionBase
{
    private boolean isNotIn;
    private EPStatementObjectModel model;

    /**
     * Ctor - for use to create an expression tree, without child expression.
     * @param model is the lookup statement object model
     * @param isNotIn is true for not-in
     */
    public SubqueryInExpression(EPStatementObjectModel model, boolean isNotIn)
    {
        this.model = model;
        this.isNotIn = isNotIn;
    }

    /**
     * Ctor - for use to create an expression tree, without child expression.
     * @param expression is the expression providing the value to match
     * @param model is the lookup statement object model
     * @param isNotIn is true for not-in
     */
    public SubqueryInExpression(Expression expression, EPStatementObjectModel model, boolean isNotIn)
    {
        this.getChildren().add(expression);
        this.model = model;
        this.isNotIn = isNotIn;
    }

    /**
     * Returns true for not-in, or false for in-lookup.
     * @return true for not-in
     */
    public boolean isNotIn()
    {
        return isNotIn;
    }

    /**
     * Set to true for not-in, or false for in-lookup.
     * @param notIn true for not-in
     */
    public void setNotIn(boolean notIn)
    {
        isNotIn = notIn;
    }

    public void toEPL(StringWriter writer)
    {
        this.getChildren().get(0).toEPL(writer);
        if (isNotIn)
        {
            writer.write(" not in (");
        }
        else
        {
            writer.write(" in (");
        }
        writer.write(model.toEPL());
        writer.write(')');
    }

    /**
     * Returns the lookup statement object model.
     * @return lookup model
     */
    public EPStatementObjectModel getModel()
    {
        return model;
    }

    /**
     * Sets the lookup statement object model.
     * @param model is the lookup model to set
     */
    public void setModel(EPStatementObjectModel model)
    {
        this.model = model;
    }
}
