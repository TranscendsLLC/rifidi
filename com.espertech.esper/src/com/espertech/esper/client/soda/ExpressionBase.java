/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.util.ArrayList;
import java.util.List;

/**
 * Base expression.
 */
public abstract class ExpressionBase implements Expression
{
    private static final long serialVersionUID = 0L;

    private List<Expression> children;

    /**
     * Ctor.
     */
    protected ExpressionBase()
    {
        children = new ArrayList<Expression>();
    }

    /**
     * Returns the list of sub-expressions to the current expression.
     * @return list of child expressions
     */
    public List<Expression> getChildren()
    {
        return children;
    }

    /**
     * Adds a new child expression to the current expression.
     * @param expression to add
     */
    protected void addChild(Expression expression)
    {
        children.add(expression);
    }
}
