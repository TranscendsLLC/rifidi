/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.std;

import com.espertech.esper.view.*;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.ViewResourceCallback;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.core.StatementContext;

import java.util.List;
import java.util.ArrayList;

/**
 * Factory for {@link GroupByView} instances.
 */
public class GroupByViewFactory implements ViewFactory
{
    /**
     * View parameters.
     */
    protected List<ExprNode> viewParameters;

    /**
     * List of criteria expressions.
     */
    protected ExprNode[] criteriaExpressions;

    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        this.viewParameters = expressionParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        criteriaExpressions = ViewFactorySupport.validate("Group-by view", parentEventType, statementContext, viewParameters, false);

        if (criteriaExpressions.length == 0)
        {
            String errorMessage = "Unique-by view requires a one or more expressions provinding unique values as parameters";
            throw new ViewParameterException(errorMessage);
        }

        this.eventType = parentEventType;
    }

    /**
     * Returns the names of fields to group by
     * @return field names
     */
    public ExprNode[] getCriteriaExpressions()
    {
        return criteriaExpressions;
    }

    public boolean canProvideCapability(ViewCapability viewCapability)
    {
        return false;
    }

    public void setProvideCapability(ViewCapability viewCapability, ViewResourceCallback resourceCallback)
    {
        throw new UnsupportedOperationException("View capability " + viewCapability.getClass().getSimpleName() + " not supported");
    }

    public View makeView(StatementContext statementContext)
    {
        return new GroupByView(statementContext, criteriaExpressions);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    /**
     * Parses the given view parameters into a list of field names to group-by.
     * @param viewParameters is the raw parameter objects
     * @param viewName is the name of the view
     * @return field names
     * @throws ViewParameterException thrown to indicate a parameter problem
     */
    protected static String[] getFieldNameParams(List<Object> viewParameters, String viewName) throws ViewParameterException
    {
        String[] fieldNames;

        String errorMessage = '\'' + viewName + "' view requires a list of String values or a String-array as parameter";
        if (viewParameters.isEmpty())
        {
            throw new ViewParameterException(errorMessage);
        }

        if (viewParameters.size() > 1)
        {
            List<String> fields = new ArrayList<String>();
            for (Object param : viewParameters)
            {
                if (!(param instanceof String))
                {
                    throw new ViewParameterException(errorMessage);
                }
                fields.add((String) param);
            }
            fieldNames = fields.toArray(new String[fields.size()]);
        }
        else
        {
            Object param = viewParameters.get(0);
            if (param instanceof String[])
            {
                String[] arr = (String[]) param;
                if (arr.length == 0)
                {
                    throw new ViewParameterException(errorMessage);
                }
                fieldNames = arr;
            }
            else if (param instanceof String)
            {
                fieldNames = new String[] {(String)param};
            }
            else
            {
                throw new ViewParameterException(errorMessage);
            }
        }

        return fieldNames;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof GroupByView))
        {
            return false;
        }

        GroupByView myView = (GroupByView) view;
        if (!ExprNodeUtility.deepEquals(myView.getCriteriaExpressions(), criteriaExpressions))
        {
            return false;
        }

        return true;
    }
}
