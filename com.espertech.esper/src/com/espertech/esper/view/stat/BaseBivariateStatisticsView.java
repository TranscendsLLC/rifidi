/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.stat;

import com.espertech.esper.collection.SingleEventIterator;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.view.ViewSupport;

import java.util.Iterator;

/**
 * View for computing statistics that require 2 input variable arrays containing X and Y datapoints.
 * Subclasses compute correlation or regression values, for instance.
 */
public abstract class BaseBivariateStatisticsView extends ViewSupport
{
    /**
     * This bean can be overridden by subclasses providing extra values such as correlation, regression.
     */
    protected BaseStatisticsBean statisticsBean;

    private ExprNode expressionX;
    private ExprNode expressionY;
    private EventBean[] eventsPerStream = new EventBean[1];

    /**
     * Services required by implementing classes.
     */
    protected StatementContext statementContext;

    private EventBean lastNewEvent;

    /**
     * Constructor requires the name of the two fields to use in the parent view to compute the statistics.
     * @param statisticsBean is the base class prodiving sum of X and Y and squares for use by subclasses
     * @param expressionX is the expression to get the X values from
     * @param expressionY is the expression to get the Y values from
     * @param statementContext contains required view services
     */
    public BaseBivariateStatisticsView(StatementContext statementContext,
                                       BaseStatisticsBean statisticsBean,
                                       ExprNode expressionX,
                                       ExprNode expressionY)
    {
        this.statementContext = statementContext;
        this.statisticsBean = statisticsBean;
        this.expressionX = expressionX;
        this.expressionY = expressionY;
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        // If we have child views, keep a reference to the old values, so we can fireStatementStopped them as old data event.
        BaseStatisticsBean oldValues = null;
        if (lastNewEvent == null)
        {
            if (this.hasViews())
            {
                oldValues = (BaseStatisticsBean) statisticsBean.clone();
            }
        }

        // add data points to the bean
        if (newData != null)
        {
            for (int i = 0; i < newData.length; i++)
            {
                eventsPerStream[0] = newData[i];
                double X = ((Number) expressionX.evaluate(eventsPerStream, true)).doubleValue();
                double Y = ((Number) expressionY.evaluate(eventsPerStream, true)).doubleValue();
                statisticsBean.addPoint(X, Y);
            }
        }

        // remove data points from the bean
        if (oldData != null)
        {
            for (int i = 0; i < oldData.length; i++)
            {
                eventsPerStream[0] = oldData[i];
                double X = ((Number) expressionX.evaluate(eventsPerStream, true)).doubleValue();
                double Y = ((Number) expressionY.evaluate(eventsPerStream, true)).doubleValue();
                statisticsBean.removePoint(X, Y);
            }
        }

        // If there are child view, fireStatementStopped update method
        if (this.hasViews())
        {
            if (lastNewEvent == null)
            {
                // Make a copy of the current values since if we change the values subsequently, the handed-down
                // values should not change
                BaseStatisticsBean newValues = (BaseStatisticsBean) statisticsBean.clone();
                EventBean newValuesEvent = statementContext.getEventAdapterService().adapterForBean(newValues);
                EventBean oldValuesEvent = statementContext.getEventAdapterService().adapterForBean(oldValues);
                updateChildren(new EventBean[] {newValuesEvent}, new EventBean[] {oldValuesEvent});
                lastNewEvent = newValuesEvent;
            }
            else
            {
                // Make a copy of the current values since if we change the values subsequently, the handed-down
                // values should not change
                BaseStatisticsBean newValues = (BaseStatisticsBean) statisticsBean.clone();
                EventBean newValuesEvent = statementContext.getEventAdapterService().adapterForBean(newValues);
                updateChildren(new EventBean[] {newValuesEvent}, new EventBean[] {lastNewEvent});
                lastNewEvent = newValuesEvent;
            }
        }
    }

    public final Iterator<EventBean> iterator()
    {
        return new SingleEventIterator(statementContext.getEventAdapterService().adapterForBean(statisticsBean));
    }

    /**
     * Returns the expression supplying X data points.
     * @return X expression
     */
    public final ExprNode getExpressionX()
    {
        return expressionX;
    }

    /**
     * Returns the expression supplying Y data points.
     * @return Y expression
     */
    public final ExprNode getExpressionY()
    {
        return expressionY;
    }
}
