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
import com.espertech.esper.client.EventBean;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for reading and filtering a source event iterator.
 */
public class FilterExprViewIterator implements Iterator<EventBean>
{
    private final Iterator<EventBean> sourceIterator;
    private final ExprEvaluator filter;
    private final EventBean[] evalEventArr;

    private EventBean nextResult;

    /**
     * Ctor.
     * @param sourceIterator is the iterator supplying events to filter out.
     * @param filter is the filter expression
     */
    public FilterExprViewIterator(Iterator<EventBean> sourceIterator, ExprEvaluator filter)
    {
        this.sourceIterator = sourceIterator;
        this.filter = filter;
        evalEventArr = new EventBean[1];
    }

    public boolean hasNext()
    {
        if (nextResult != null)
        {
            return true;
        }
        findNext();
        if (nextResult != null)
        {
            return true;
        }
        return false;
    }

    public EventBean next()
    {
        if (nextResult != null)
        {
            EventBean result = nextResult;
            nextResult = null;
            return result;
        }
        findNext();
        if (nextResult != null)
        {
            EventBean result = nextResult;
            nextResult = null;
            return result;
        }
        throw new NoSuchElementException();
    }

    private void findNext()
    {
        while(sourceIterator.hasNext())
        {
            EventBean candidate = sourceIterator.next();
            evalEventArr[0] = candidate;

            Boolean pass = (Boolean) filter.evaluate(evalEventArr, true);
            if ((pass != null) && (pass))
            {
                nextResult = candidate;
                break;
            }
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
