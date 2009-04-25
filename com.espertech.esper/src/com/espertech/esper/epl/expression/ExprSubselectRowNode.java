/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.spec.StatementSpecRaw;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.schedule.TimeProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * Represents a subselect in an expression tree.
 */
public class ExprSubselectRowNode extends ExprSubselectNode
{
    private static final Log log = LogFactory.getLog(ExprSubselectRowNode.class);

    /**
     * Ctor.
     * @param statementSpec is the lookup statement spec from the parser, unvalidated
     */
    public ExprSubselectRowNode(StatementSpecRaw statementSpec)
    {
        super(statementSpec);
    }

    public Class getType()
    {
        if (selectClause == null)   // wildcards allowed
        {
            return rawEventType.getUnderlyingType();
        }
        return selectClause.getType();
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Set<EventBean> matchingEvents)
    {
        if (matchingEvents == null)
        {
            return null;
        }
        if (matchingEvents.size() == 0)
        {
            return null;
        }
        if ((filterExpr == null) && (matchingEvents.size() > 1))
        {
            log.warn("Subselect returned more then one row in subselect '" + toExpressionString() + "', returning null result");
            return null;
        }

        // Evaluate filter
        EventBean subSelectResult = null;
        EventBean[] events = new EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);

        if (filterExpr != null)
        {
            for (EventBean subselectEvent : matchingEvents)
            {
                // Prepare filter expression event list
                events[0] = subselectEvent;

                Boolean pass = (Boolean) filterExpr.evaluate(events, true);
                if ((pass != null) && (pass))
                {
                    if (subSelectResult != null)
                    {
                        log.warn("Subselect returned more then one row in subselect '" + toExpressionString() + "', returning null result");
                        return null;
                    }
                    subSelectResult = subselectEvent;
                }
            }

            if (subSelectResult == null)
            {
                return null;
            }
        }
        else
        {
            subSelectResult = matchingEvents.iterator().next();
        }

        events[0] = subSelectResult;
        Object result;

        if (selectClause != null)
        {
            result = selectClause.evaluate(events, true);
        }
        else
        {
            result = events[0].getUnderlying();
        }

        return result;
    }
}
