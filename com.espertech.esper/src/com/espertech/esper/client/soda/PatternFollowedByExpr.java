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
 * Followed-by for use in pattern expressions. 
 */
public class PatternFollowedByExpr extends PatternExprBase
{
    /**
     * Ctor - for use to create a pattern expression tree, without pattern child expression.
     */
    public PatternFollowedByExpr()
    {
    }

    /**
     * Ctor.
     * @param first a first pattern expression in the followed-by relationship
     * @param second a second pattern expression in the followed-by relationship
     * @param patternExprs further optional pattern expressions in the followed-by relationship
     */
    public PatternFollowedByExpr(PatternExpr first, PatternExpr second, PatternExpr ...patternExprs)
    {
        this.addChild(first);
        this.addChild(second);
        for (int i = 0; i < patternExprs.length; i++)
        {
            this.addChild(patternExprs[i]);
        }
    }

    /**
     * Adds a pattern expression to the followed-by relationship between patterns.
     * @param expr to add
     * @return pattern expression
     */
    public PatternFollowedByExpr add(PatternExpr expr)
    {
        this.getChildren().add(expr);
        return this;
    }

    public void toEPL(StringWriter writer)
    {
        String delimiter = "";
        for (PatternExpr child : this.getChildren())
        {
            writer.write(delimiter);
            writer.write('(');
            child.toEPL(writer);
            writer.write(')');
            delimiter = " -> ";
        }
    }
}
