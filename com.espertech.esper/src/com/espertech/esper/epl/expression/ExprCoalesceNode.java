/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.CoercionException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents the COALESCE(a,b,...) function is an expression tree.
 */
public class ExprCoalesceNode extends ExprNode
{
    private Class resultType;
    private boolean[] isNumericCoercion;

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        if (this.getChildNodes().size() < 2)
        {
            throw new ExprValidationException("Coalesce node must have at least 2 child nodes");
        }

        // get child expression types
        Class[] childTypes = new Class[getChildNodes().size()];
        int count = 0;
        for (ExprNode child : this.getChildNodes())
        {
            childTypes[count] = child.getType();
            count++;
        }

        // determine coercion type
        try {
            resultType = JavaClassHelper.getCommonCoercionType(childTypes);
        }
        catch (CoercionException ex)
        {
            throw new ExprValidationException("Implicit conversion not allowed: " + ex.getMessage());
        }

        // determine which child nodes need numeric coercion
        isNumericCoercion = new boolean[getChildNodes().size()];
        count = 0;
        for (ExprNode child : this.getChildNodes())
        {
            if ((JavaClassHelper.getBoxedType(child.getType()) != resultType) &&
                (child.getType() != null) && (resultType != null))
            {
                if (!JavaClassHelper.isNumeric(resultType))
                {
                    throw new ExprValidationException("Implicit conversion from datatype '" +
                            resultType.getSimpleName() +
                            "' to " + child.getType() + " is not allowed");
                }
                isNumericCoercion[count] = true;
            }
            count++;
        }
    }

    public boolean isConstantResult()
    {
        return false;
    }    

    public Class getType()
    {
        return resultType;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        Object value = null;

        // Look for the first non-null return value
        int count = 0;
        for (ExprNode childNode : this.getChildNodes())
        {
            value = childNode.evaluate(eventsPerStream, isNewData);

            if (value != null)
            {
                // Check if we need to coerce
                if (isNumericCoercion[count])
                {
                    return JavaClassHelper.coerceBoxed((Number)value, resultType);
                }
                return value;
            }
            count++;
        }

        return null;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("coalesce(");

        String delimiter = "";
        for (int i = 0; i < this.getChildNodes().size(); i++)
        {
            buffer.append(delimiter);
            buffer.append(this.getChildNodes().get(i).toExpressionString());
            delimiter = ",";
        }
        buffer.append(')');
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprCoalesceNode))
        {
            return false;
        }

        return true;
    }
}
