/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents an OR expression in a filter expression tree.
 */
public class ExprOrNode extends ExprNode
{
    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        // Sub-nodes must be returning boolean
        for (ExprNode child : this.getChildNodes())
        {
            Class childType = child.getType();
            if (!JavaClassHelper.isBoolean(childType))
            {
                throw new ExprValidationException("Incorrect use of OR clause, sub-expressions do not return boolean");
            }
        }

        if (this.getChildNodes().size() <= 1)
        {
            throw new ExprValidationException("The OR operator requires at least 2 child expressions");
        }
    }

    public Class getType()
    {
        return Boolean.class;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        // At least one child must evaluate to true
        for (ExprNode child : this.getChildNodes())
        {
            Boolean evaluated = (Boolean) child.evaluate(eventsPerStream, isNewData);
            if (evaluated)
            {
                return true;
            }
        }
        return false;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append('(');

        String appendStr = "";
        for (ExprNode child : this.getChildNodes())
        {
            buffer.append(appendStr);
            buffer.append(child.toExpressionString());
            appendStr = " OR ";
        }

        buffer.append(')');
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprOrNode))
        {
            return false;
        }

        return true;
    }
}
