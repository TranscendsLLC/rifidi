/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern.guard;

import com.espertech.esper.pattern.PatternContext;
import com.espertech.esper.pattern.MatchedEventConvertor;
import com.espertech.esper.pattern.MatchedEventMap;
import com.espertech.esper.epl.expression.ExprNode;

import java.util.List;

/**
 * Interface for a factory for {@link Guard} instances.
 */
public interface GuardFactory
{
    /**
     * Sets the guard object parameters.
     * @param guardParameters is a list of parameters
     * @param convertor for converting a 
     * @throws GuardParameterException thrown to indicate a parameter problem
     */
    public void setGuardParameters(List<ExprNode> guardParameters, MatchedEventConvertor convertor) throws GuardParameterException;

    /**
     * Constructs a guard instance.
     * @param context - services for use by guard
     * @param beginState - the prior matching events
     * @param quitable - to use for indicating the guard has quit
     * @param stateNodeId - a node id for the state object
     * @param guardState - state node for guard
     * @return guard instance
     */
    public Guard makeGuard(PatternContext context,
                           MatchedEventMap beginState,
                           Quitable quitable,
                           Object stateNodeId,
                           Object guardState);
}
