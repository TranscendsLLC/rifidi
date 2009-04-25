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
 * Represents a plug-in aggregation function.
 */
public class PlugInProjectionExpression extends ExpressionBase
{
    private String functionName;
    private boolean isDistinct;

    /**
     * Ctor.
     * @param functionName the name of the function
     * @param isDistinct true for distinct
     */
    public PlugInProjectionExpression(String functionName, boolean isDistinct)
    {
        this.functionName = functionName;
        this.isDistinct = isDistinct;
    }

    /**
     * Ctor.
     * @param functionName the name of the function
     * @param isDistinct true for distinct
     * @param expression provides aggregated values
     */
    public PlugInProjectionExpression(String functionName, boolean isDistinct, Expression expression)
    {
        this.functionName = functionName;
        this.isDistinct = isDistinct;
        this.getChildren().add(expression);
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPL(StringWriter writer)
    {
        writer.write(functionName);
        writer.write('(');
        if (isDistinct)
        {
            writer.write("distinct ");
        }
        if (this.getChildren().size() > 0)
        {
            this.getChildren().get(0).toEPL(writer);
        }
        writer.write(")");
    }

    /**
     * Returns the function name.
     * @return name of function
     */
    public String getFunctionName()
    {
        return functionName;
    }

    /**
     * Sets the function name.
     * @param functionName name of function
     */
    public void setFunctionName(String functionName)
    {
        this.functionName = functionName;
    }

    /**
     * Returns true for distinct.
     * @return boolean indicating distinct or not
     */
    public boolean isDistinct()
    {
        return isDistinct;
    }

    /**
     * Set to true for distinct.
     * @param distinct indicating distinct or not
     */
    public void setDistinct(boolean distinct)
    {
        isDistinct = distinct;
    }
}
