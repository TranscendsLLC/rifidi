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
 * Exists-expression for a set of values returned by a lookup.
 */
public class SubqueryQualifiedExpression extends ExpressionBase
{
    private EPStatementObjectModel model;
    private String operator;
    private boolean isAll;

    /**
     * Ctor - for use to create an expression tree, without child expression.
     * @param model is the lookup statement object model
     * @param operator the op
     * @param all true for ALL, false for ANY
     */
    public SubqueryQualifiedExpression(EPStatementObjectModel model, String operator, boolean all)
    {
        this.model = model;
        this.operator = operator;
        isAll = all;
    }

    public void toEPL(StringWriter writer)
    {
        getChildren().get(0).toEPL(writer);
        writer.write(' ');
        writer.write(operator);
        if (isAll)
        {
            writer.write(" all (");
        }
        else
        {
            writer.write(" any (");
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

    /**
     * Returns the operator.
     * @return operator
     */
    public String getOperator()
    {
        return operator;
    }

    /**
     * Sets the operator.
     * @param operator op
     */
    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    /**
     * Returns true for ALL, false for ANY.
     * @return all/any flag
     */
    public boolean isAll()
    {
        return isAll;
    }

    /**
     * Set to true for ALL, false for ANY.
     * @param all true for ALL
     */
    public void setAll(boolean all)
    {
        isAll = all;
    }
}