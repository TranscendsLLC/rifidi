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
 * Negates the contained-within subexpression.
 * <p>
 * Has a single child expression to be negated.
 */
public class NotExpression extends ExpressionBase
{
    /**
     * Ctor.
     * @param inner is the expression to negate
     */
    public NotExpression(Expression inner)
    {
        this.addChild(inner);
    }

    /**
     * Ctor - for use to create an expression tree, without child expression.
     */
    public NotExpression()
    {        
    }

    public void toEPL(StringWriter writer)
    {
        writer.write("not ");
        this.getChildren().get(0).toEPL(writer);
    }
}
