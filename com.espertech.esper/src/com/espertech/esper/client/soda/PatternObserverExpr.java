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
import java.util.Arrays;

/**
 * Pattern observer expression observes occurances such as timer-at (crontab) and timer-interval. 
 */
public class PatternObserverExpr extends EPBaseNamedObject implements PatternExpr
{
    private static final long serialVersionUID = 0L;

    /**
     * Ctor - for use to create a pattern expression tree, without pattern child expression.
     * @param namespace is the guard object namespace
     * @param name is the guard object name
     * @param parameters is guard object parameters
     */
    public PatternObserverExpr(String namespace, String name, Expression[] parameters)
    {
        super(namespace, name, Arrays.asList(parameters));
    }

    /**
     * Ctor - for use to create a pattern expression tree, without pattern child expression.
     * @param namespace is the guard object namespace
     * @param name is the guard object name
     * @param parameters is guard object parameters
     */
    public PatternObserverExpr(String namespace, String name, List<Expression> parameters)
    {
        super(namespace, name, parameters);
    }

    public List<PatternExpr> getChildren()
    {
        return new ArrayList<PatternExpr>();
    }
}
