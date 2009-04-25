/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;

/**
 * Processor for select-clause expressions that handles wildcards for single streams with no insert-into.
 */
public class SelectExprWildcardProcessor implements SelectExprProcessor
{
    private final EventType eventType;

    /**
     * Ctor.
     * @param eventType is the type of event this processor produces
     * @throws com.espertech.esper.epl.expression.ExprValidationException if the expression validation failed
     */
    public SelectExprWildcardProcessor(EventType eventType) throws ExprValidationException
    {
        this.eventType = eventType;
    }

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize)
    {
        return eventsPerStream[0];
    }

    public EventType getResultEventType()
    {
        return eventType;
    }
}
