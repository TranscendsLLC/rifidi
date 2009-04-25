/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.stat;

import com.espertech.esper.view.*;
import com.espertech.esper.collection.SingleEventIterator;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * View for computing a weighted average. The view uses 2 fields within the parent view to compute the weighted average.
 * The X field and weight field. In a price-volume example it calculates the volume-weighted average price
 * as   (sum(price * volume) / sum(volume)).
 * Example: weighted_avg("price", "volume")
 */
public final class WeightedAverageView extends ViewSupport implements CloneableView
{
    private final EventType eventType;
    private final StatementContext statementContext;
    private final ExprNode fieldNameX;
    private final ExprNode fieldNameWeight;
    private EventBean[] eventsPerStream = new EventBean[1];

    private double sumXtimesW = Double.NaN;
    private double sumW = Double.NaN;
    private double currentValue = Double.NaN;

    private EventBean lastNewEvent;

    /**
     * Constructor requires the name of the field to use in the parent view to compute the weighted average on,
     * as well as the name of the field in the parent view to get the weight from.
     * @param fieldNameX is the name of the field within the parent view to use to get numeric data points for this view to
     * compute the average for.
     * @param fieldNameWeight is the field name for the weight to apply to each data point
     * @param statementContext contains required view services
     */
    public WeightedAverageView(StatementContext statementContext, ExprNode fieldNameX, ExprNode fieldNameWeight)
    {
        this.fieldNameX = fieldNameX;
        this.fieldNameWeight = fieldNameWeight;
        this.statementContext = statementContext;
        eventType = createEventType(statementContext);
    }

    public View cloneView(StatementContext statementContext)
    {
        return new WeightedAverageView(statementContext, fieldNameX, fieldNameWeight);
    }

    /**
     * Returns the expression supplying the X values.
     * @return expression supplying X data points
     */
    public final ExprNode getFieldNameX()
    {
        return fieldNameX;
    }

    /**
     * Returns the expression supplying the weight values.
     * @return expression supplying weight
     */
    public final ExprNode getFieldNameWeight()
    {
        return fieldNameWeight;
    }

    public final void update(EventBean[] newData, EventBean[] oldData)
    {
        double oldValue = currentValue;

        // add data points to the bean
        if (newData != null)
        {
            for (int i = 0; i < newData.length; i++)
            {
                eventsPerStream[0] = newData[i];
                double point = ((Number) fieldNameX.evaluate(eventsPerStream, true)).doubleValue();
                double weight = ((Number) fieldNameWeight.evaluate(eventsPerStream, true)).doubleValue();

                if (Double.valueOf(sumXtimesW).isNaN())
                {
                    sumXtimesW = point * weight;
                    sumW = weight;
                }
                else
                {
                    sumXtimesW += point * weight;
                    sumW += weight;
                }
            }
        }

        // remove data points from the bean
        if (oldData != null)
        {
            for (int i = 0; i < oldData.length; i++)
            {
                eventsPerStream[0] = oldData[i];
                double point = ((Number) fieldNameX.evaluate(eventsPerStream, true)).doubleValue();
                double weight = ((Number) fieldNameWeight.evaluate(eventsPerStream, true)).doubleValue();

                sumXtimesW -= point * weight;
                sumW -= weight;
            }
        }

        if (sumW != 0)
        {
            currentValue = sumXtimesW / sumW;
        }
        else
        {
            currentValue = Double.NaN;
        }

        // If there are child view, fireStatementStopped update method
        if (this.hasViews())
        {
            Map<String, Object> newDataMap = new HashMap<String, Object>();
            newDataMap.put(ViewFieldEnum.WEIGHTED_AVERAGE__AVERAGE.getName(), currentValue);
            EventBean newDataEvent = statementContext.getEventAdapterService().adaptorForTypedMap(newDataMap, eventType);

            if (lastNewEvent == null)
            {
                Map<String, Object> oldDataMap = new HashMap<String, Object>();
                oldDataMap.put(ViewFieldEnum.WEIGHTED_AVERAGE__AVERAGE.getName(), oldValue);
                EventBean oldDataEvent = statementContext.getEventAdapterService().adaptorForTypedMap(oldDataMap, eventType);

                updateChildren(new EventBean[] {newDataEvent}, new EventBean[] {oldDataEvent});
            }
            else
            {
                updateChildren(new EventBean[] {newDataEvent}, new EventBean[] {lastNewEvent});
            }
            lastNewEvent = newDataEvent;
        }
    }

    public final EventType getEventType()
    {
        return eventType;
    }

    public final Iterator<EventBean> iterator()
    {
        Map<String, Object> newDataMap = new HashMap<String, Object>();
        newDataMap.put(ViewFieldEnum.WEIGHTED_AVERAGE__AVERAGE.getName(), currentValue);
        return new SingleEventIterator(statementContext.getEventAdapterService().adaptorForTypedMap(newDataMap, eventType));
    }

    public final String toString()
    {
        return this.getClass().getName() +
                " fieldName=" + fieldNameX +
                " fieldNameWeight=" + fieldNameWeight;
    }

    /**
     * Creates the event type for this view.
     * @param statementContext is the event adapter service
     * @return event type of view
     */
    protected static EventType createEventType(StatementContext statementContext)
    {
        Map<String, Object> schemaMap = new HashMap<String, Object>();
        schemaMap.put(ViewFieldEnum.WEIGHTED_AVERAGE__AVERAGE.getName(), double.class);
        return statementContext.getEventAdapterService().createAnonymousMapType(schemaMap);
    }
}
