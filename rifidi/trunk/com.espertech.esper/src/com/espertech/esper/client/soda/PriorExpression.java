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
 * Expression representing the prior function.
 */
public class PriorExpression extends ExpressionBase
{
    /**
     * Ctor - for use to create an expression tree, without child expression.
     */
    public PriorExpression()
    {
    }

    /**
     * Ctor.
     * @param index is the index of the prior event
     * @param propertyName is the property to return
     */
    public PriorExpression(int index, String propertyName)
    {
        this.addChild(new ConstantExpression(index));
        this.addChild(new PropertyValueExpression(propertyName));
    }

    public void toEPL(StringWriter writer)
    {
        writer.write("prior(");
        this.getChildren().get(0).toEPL(writer);
        writer.write(", ");
        this.getChildren().get(1).toEPL(writer);
        writer.write(')');
    }
}
