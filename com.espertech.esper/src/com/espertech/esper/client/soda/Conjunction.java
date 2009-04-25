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
 * Conjunction represents a logical AND allowing multiple sub-expressions to be connected by AND.
 */
public class Conjunction extends Junction
{    
    /**
     * Ctor - for use to create an expression tree, without child expression.
     * <p>
     * Use add methods to add child expressions to acts upon.
     */
    public Conjunction()
    {
    }

    /**
     * Ctor.
     * @param first provides value to AND
     * @param second provides value to AND
     * @param expressions is more expressions to put in the AND-relationship.
     */
    public Conjunction(Expression first, Expression second, Expression ...expressions)
    {
        addChild(first);
        addChild(second);
        for (int i = 0; i < expressions.length; i++)
        {
            addChild(expressions[i]);
        }
    }

    public void toEPL(StringWriter writer)
    {
        String delimiter = "";
        for (Expression child : this.getChildren())
        {
            writer.write(delimiter);
            writer.write('(');
            child.toEPL(writer);
            writer.write(')');
            delimiter = " and ";
        }
    }    
}
