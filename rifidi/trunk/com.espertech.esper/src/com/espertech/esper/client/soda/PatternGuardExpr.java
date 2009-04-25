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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Guard is the where timer-within pattern object for use in pattern expressions. 
 */
public class PatternGuardExpr extends EPBaseNamedObject implements PatternExpr
{
    private static final long serialVersionUID = 0L;

    private List<PatternExpr> guarded;

    /**
     * Ctor - for use to create a pattern expression tree, without pattern child expression.
     * @param namespace is the guard object namespace
     * @param name is the guard object name
     * @param parameters is guard object parameters
     */
    public PatternGuardExpr(String namespace, String name, List<Expression> parameters)
    {
        super(namespace, name, parameters);
        this.guarded = new ArrayList<PatternExpr>();
    }

    /**
     * Ctor - for use to create a pattern expression tree, without pattern child expression.
     * @param namespace is the guard object namespace
     * @param name is the guard object name
     * @param parameters is guard object parameters
     * @param guarded is the guarded pattern expression
     */
    public PatternGuardExpr(String namespace, String name, Expression[] parameters, PatternExpr guarded)
    {
        this(namespace, name, Arrays.asList(parameters), guarded);
    }

    /**
     * Ctor - for use to create a pattern expression tree, without pattern child expression.
     * @param namespace is the guard object namespace
     * @param name is the guard object name
     * @param parameters is guard object parameters
     * @param guardedPattern is the guarded pattern expression
     */
    public PatternGuardExpr(String namespace, String name, List<Expression> parameters, PatternExpr guardedPattern)
    {
        super(namespace, name, parameters);
        this.guarded = new ArrayList<PatternExpr>();
        guarded.add(guardedPattern);
    }

    public List<PatternExpr> getChildren()
    {
        return guarded;
    }

    public void toEPL(StringWriter writer)
    {
        writer.write('(');
        guarded.get(0).toEPL(writer);
        writer.write(") where ");
        super.toEPL(writer);
    }
}
