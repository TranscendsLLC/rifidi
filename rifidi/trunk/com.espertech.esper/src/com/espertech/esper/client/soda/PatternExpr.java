/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

/**
 * Interface representing a pattern expression.
 * <p>
 * Pattern expressions are organized into a tree-like structure with nodes representing sub-expressions (composite).
 * <p>
 * Certain types of nodes have certain requirements towards the number or types of nodes that
 * are expected as pattern sub-expressions to an pattern expression.
 */
public interface PatternExpr extends Serializable
{
    /**
     * Returns the list of pattern sub-expressions (child expressions) to the current pattern expression node.
     * @return pattern child expressions or empty list if there are no child expressions
     */
    public List<PatternExpr> getChildren();

    /**
     * Renders the pattern expression and all it's child expressions, in full tree depth, as a string in
     * language syntax.
     * @param writer is the output to use
     */    
    public void toEPL(StringWriter writer);
}
