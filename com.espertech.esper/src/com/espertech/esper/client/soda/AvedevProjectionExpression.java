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
 * Mean deviation of the (distinct) values returned by an expression. 
 */
public class AvedevProjectionExpression extends ExpressionBase
{
    private boolean isDistinct;

    /**
     * Ctor - for use to create an expression tree, without inner expression. 
     * @param isDistinct true if distinct
     */
    public AvedevProjectionExpression(boolean isDistinct)
    {
        this.isDistinct = isDistinct;
    }

    /**
     * Ctor - adds the expression to project.
     * @param expression returning values to project
     * @param isDistinct true if distinct
     */
    public AvedevProjectionExpression(Expression expression, boolean isDistinct)
    {
        this.isDistinct = isDistinct;
        this.getChildren().add(expression);
    }

    public void toEPL(StringWriter writer)
    {
        writer.write("avedev(");
        if (isDistinct)
        {
            writer.write("distinct ");
        }
        this.getChildren().get(0).toEPL(writer);
        writer.write(")");
    }

    /**
     * Returns true if the projection considers distinct values only.
     * @return true if distinct
     */
    public boolean isDistinct()
    {
        return isDistinct;
    }

    /**
     * Set the distinct flag indicating the projection considers distinct values only.
     * @param distinct true for distinct, false for not distinct
     */
    public void setDistinct(boolean distinct)
    {
        isDistinct = distinct;
    }
}
