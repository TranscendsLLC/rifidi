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
 * SQL-Like expression for matching '%' and '_' wildcard strings following SQL standards. 
 */
public class LikeExpression extends ExpressionBase
{
    private boolean isNot;

    /**
     * Ctor - for use to create an expression tree, without child expression.
     * <p>
     * Use add methods to add child expressions to acts upon.
     */
    public LikeExpression()
    {
        isNot = false;
    }

    /**
     * Ctor - for use to create an expression tree, without child expression.
     * <p>
     * Use add methods to add child expressions to acts upon.
     * @param isNot if the like-expression is negated
     */
    public LikeExpression(boolean isNot)
    {
        this.isNot = isNot;
    }

    /**
     * Ctor.
     * @param left provides the value to match
     * @param right provides the like-expression to match against
     */
    public LikeExpression(Expression left, Expression right)
    {
        this(left, right, null);
    }

    /**
     * Ctor.
     * @param left provides the value to match
     * @param right provides the like-expression to match against
     * @param escape is the expression providing the string escape character
     */
    public LikeExpression(Expression left, Expression right, Expression escape)
    {
        this.getChildren().add(left);
        this.getChildren().add(right);
        if (escape != null)
        {
            this.getChildren().add(escape);
        }
        this.isNot = false;
    }

    /**
     * Ctor.
     * @param left provides the value to match
     * @param right provides the like-expression to match against
     * @param isNot if the like-expression is negated
     */
    public LikeExpression(Expression left, Expression right, boolean isNot)
    {
        this(left, right, null, isNot);
    }

    /**
     * Ctor.
     * @param left provides the value to match
     * @param right provides the like-expression to match against
     * @param escape is the expression providing the string escape character
     * @param isNot if the like-expression is negated
     */
    public LikeExpression(Expression left, Expression right, Expression escape, boolean isNot)
    {
        this.getChildren().add(left);
        this.getChildren().add(right);
        if (escape != null)
        {
            this.getChildren().add(escape);
        }
        this.isNot = isNot;
    }

    public void toEPL(StringWriter writer)
    {
        writer.write("(");
        this.getChildren().get(0).toEPL(writer);
        if (isNot)
        {
            writer.write(" not");
        }
        writer.write(" like ");
        this.getChildren().get(1).toEPL(writer);

        if (this.getChildren().size() > 2)
        {
            writer.write(" escape ");
            this.getChildren().get(2).toEPL(writer);
        }
        writer.write(")");
    }

    /**
     * Returns true if this is a "not like", or false if just a like
     * @return indicator whether negated or not
     */
    public boolean isNot()
    {
        return isNot;
    }
}
