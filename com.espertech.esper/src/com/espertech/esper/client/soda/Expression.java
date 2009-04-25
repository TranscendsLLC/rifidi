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
import java.util.Iterator;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Interface representing an expression for use in select-clauses, where-clauses, having-clauses, order-by clauses and
 * streams based on filters and pattern filter expressions.
 * <p>
 * Expressions are organized into a tree-like structure with nodes representing sub-expressions.
 * <p>
 * Certain types of nodes have certain requirements towards the number or types of nodes that
 * are expected as sub-expressions to an expression.
 */
public interface Expression extends Serializable
{
    /**
     * Returns the list of sub-expressions (child expressions) to the current expression node.
     * @return child expressions or empty list if there are no child expressions
     */
    public List<Expression> getChildren();

    /**
     * Renders the expressions and all it's child expression, in full tree depth, as a string in
     * language syntax.
     * @param writer is the output to use
     */
    public void toEPL(StringWriter writer);
}
