/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.view;

import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.view.ViewSupport;

import java.util.Iterator;

/**
 * Simple filter view filtering events using a filter expression tree.
 */
public class FilterExprView extends ViewSupport
{
    private ExprEvaluator exprEvaluator;

    /**
     * Ctor.
     * @param exprEvaluator - Filter expression evaluation impl
     */
    public FilterExprView(ExprEvaluator exprEvaluator)
    {
        this.exprEvaluator = exprEvaluator;
    }

    public EventType getEventType()
    {
        return parent.getEventType();
    }

    public Iterator<EventBean> iterator()
    {
        return new FilterExprViewIterator(parent.iterator(), exprEvaluator);
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        EventBean[] filteredNewData = filterEvents(exprEvaluator, newData, true);
        EventBean[] filteredOldData = filterEvents(exprEvaluator, oldData, false);

        if ((filteredNewData != null) || (filteredOldData != null))
        {
            this.updateChildren(filteredNewData, filteredOldData);
        }
    }

    /**
     * Filters events using the supplied evaluator.
     * @param exprEvaluator - evaluator to use
     * @param events - events to filter
     * @param isNewData - true to indicate filter new data (istream) and not old data (rstream)
     * @return filtered events, or null if no events got through the filter
     */
    protected static EventBean[] filterEvents(ExprEvaluator exprEvaluator, EventBean[] events, boolean isNewData)
    {
        if (events == null)
        {
            return null;
        }

        EventBean[] evalEventArr = new EventBean[1];
        boolean passResult[] = new boolean[events.length];
        int passCount = 0;

        for (int i = 0; i < events.length; i++)
        {
            evalEventArr[0] = events[i];
            Boolean pass = (Boolean) exprEvaluator.evaluate(evalEventArr, isNewData);
            if ((pass != null) && (pass))
            {
                passResult[i] = true;
                passCount++;
            }
        }

        if (passCount == 0)
        {
            return null;
        }
        if (passCount == events.length)
        {
            return events;
        }

        EventBean[] resultArray = new EventBean[passCount];
        int count = 0;
        for (int i = 0; i < passResult.length; i++)
        {
            if (passResult[i])
            {
                resultArray[count] = events[i];
                count++;
            }
        }
        return resultArray;
    }
}
