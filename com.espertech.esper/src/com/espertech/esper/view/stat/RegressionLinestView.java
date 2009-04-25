/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.stat;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.view.CloneableView;
import com.espertech.esper.view.View;
import com.espertech.esper.epl.expression.ExprNode;

/**
 * A view that calculates regression on two fields. The view uses internally a {@link RegressionBean}
 * instance for the calculations, it also returns this bean as the result.
 * This class accepts most of its behaviour from its parent, {@link com.espertech.esper.view.stat.BaseBivariateStatisticsView}. It adds
 * the usage of the regression bean and the appropriate schema.
 */
public final class RegressionLinestView extends BaseBivariateStatisticsView implements CloneableView
{
    private EventType eventType;

    /**
     * Constructor.
     * @param xFieldName is the field name of the field providing X data points
     * @param yFieldName is the field name of the field providing X data points
     * @param statementContext contains required view services
     */
    public RegressionLinestView(StatementContext statementContext, ExprNode xFieldName, ExprNode yFieldName)
    {
        super(statementContext, new RegressionBean(), xFieldName, yFieldName);
    }

    public View cloneView(StatementContext statementContext)
    {
        return new RegressionLinestView(statementContext, this.getExpressionX(), this.getExpressionY());
    }

    public EventType getEventType()
    {
        if (eventType == null)
        {
            eventType = createEventType(statementContext);
        }
        return eventType;
    }

    public String toString()
    {
        return this.getClass().getName() +
                " fieldX=" + this.getExpressionX() +
                " fieldY=" + this.getExpressionY();
    }

    /**
     * Creates the event type for this view.
     * @param statementContext is the event adapter service
     * @return event type of view
     */
    protected static EventType createEventType(StatementContext statementContext)
    {
        return statementContext.getEventAdapterService().addBeanType(RegressionBean.class.getName(), RegressionBean.class, false);
    }
}

