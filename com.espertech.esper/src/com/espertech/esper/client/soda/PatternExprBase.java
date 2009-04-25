/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.util.List;
import java.util.ArrayList;

/**
 * Abstract base class for all pattern expressions.
 */
public abstract class PatternExprBase implements PatternExpr
{
    private static final long serialVersionUID = 0L;

    private List<PatternExpr> children;

    /**
     * Ctor.
     */
    protected PatternExprBase()
    {
        children = new ArrayList<PatternExpr>();
    }

    public List<PatternExpr> getChildren()
    {
        return children;
    }

    /**
     * Adds a sub-expression to the pattern expression.
     * @param expression to add
     */
    protected void addChild(PatternExpr expression)
    {
        children.add(expression);
    }
}
