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
 * Property-exists checks if a dynamic property exists.
 */
public class PropertyExistsExpression extends ExpressionBase
{
    /**
     * Ctor - for use to create an expression tree, without child expression.
     */
    public PropertyExistsExpression()
    {
    }

    /**
     * Ctor.
     * @param propertyName is the name of the property to check existence
     */
    public PropertyExistsExpression(String propertyName)
    {
        this.getChildren().add(Expressions.getPropExpr(propertyName));
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPL(StringWriter writer)
    {
        writer.write("exists(");
        this.getChildren().get(0).toEPL(writer);
        writer.write(")");
    }
}
