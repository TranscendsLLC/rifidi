/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.spec.StatementSpecRaw;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;

import java.util.Set;

/**
 * Represents a subselect in an expression tree.
 */
public class ExprSubselectInNode extends ExprSubselectNode
{
    private boolean isNotIn;
    private SubselectEvalStrategy subselectEvalStrategy;

    /**
     * Ctor.
     * @param statementSpec is the lookup statement spec from the parser, unvalidated
     */
    public ExprSubselectInNode(StatementSpecRaw statementSpec)
    {
        super(statementSpec);
    }

    public Class getType()
    {
        return Boolean.class;
    }

    /**
     * Indicate that this is a not-in lookup.
     * @param notIn is true for not-in, or false for regular 'in'
     */
    public void setNotIn(boolean notIn)
    {
        isNotIn = notIn;
    }

    /**
     * Returns true for not-in, or false for in.
     * @return true for not-in
     */
    public boolean isNotIn()
    {
        return isNotIn;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        subselectEvalStrategy = SubselectEvalStrategyFactory.createStrategy(this, isNotIn, false, false, null);
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Set<EventBean> matchingEvents)
    {
        return subselectEvalStrategy.evaluate(eventsPerStream, isNewData, matchingEvents);
    }
}
