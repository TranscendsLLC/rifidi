/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.*;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents a variable in an expression tree.
 */
public class ExprVariableNode extends ExprNode
{
    private static final long serialVersionUID = 0L;

    private final String variableName;
    private Class variableType;
    private transient VariableReader reader;

    /**
     * Ctor.
     * @param variableName is the name of the variable
     */
    public ExprVariableNode(String variableName)
    {
        if (variableName == null)
        {
            throw new IllegalArgumentException("Variables name is null");
        }
        this.variableName = variableName;
    }

    /**
     * Returns the name of the variable.
     * @return variable name
     */
    public String getVariableName()
    {
        return variableName;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        reader = variableService.getReader(variableName);
        if (reader == null)
        {
            throw new ExprValidationException("A variable by name '" + variableName + " has not been declared");
        }

        // the variable name should not overlap with a property name
        try
        {
            streamTypeService.resolveByPropertyName(variableName);
            throw new ExprValidationException("The variable by name '" + variableName + " is ambigous to a property of the same name");
        }
        catch (DuplicatePropertyException e)
        {
            throw new ExprValidationException("The variable by name '" + variableName + " is ambigous to a property of the same name");
        }
        catch (PropertyNotFoundException e)
        {
            // expected
        }

        variableType = reader.getType();
    }

    public Class getType()
    {
        if (variableType == null)
        {
            throw new IllegalStateException("Variables node has not been validated");
        }
        return variableType;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public String toString()
    {
        return "variableName=" + variableName;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        return reader.getValue();
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(variableName);
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprVariableNode))
        {
            return false;
        }

        ExprVariableNode other = (ExprVariableNode) node;

        return other.variableName.equals(this.variableName);
    }
}
