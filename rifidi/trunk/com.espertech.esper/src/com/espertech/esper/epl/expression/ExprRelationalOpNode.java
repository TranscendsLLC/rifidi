/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.type.RelationalOpEnum;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents a lesser or greater then (</<=/>/>=) expression in a filter expression tree.
 */
public class ExprRelationalOpNode extends ExprNode
{
    private final RelationalOpEnum relationalOpEnum;
    private RelationalOpEnum.Computer computer;

    /**
     * Ctor.
     * @param relationalOpEnum - type of compare, ie. lt, gt, le, ge
     */
    public ExprRelationalOpNode(RelationalOpEnum relationalOpEnum)
    {
        this.relationalOpEnum = relationalOpEnum;
    }

    public boolean isConstantResult()
    {
        return false;
    }
    
    /**
     * Returns the type of relational op used.
     * @return enum with relational op type
     */
    public RelationalOpEnum getRelationalOpEnum()
    {
        return relationalOpEnum;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        // Must have 2 child nodes
        if (this.getChildNodes().size() != 2)
        {
            throw new IllegalStateException("Relational op node does not have exactly 2 child nodes");
        }

        // Must be either numeric or string
        Class typeOne = JavaClassHelper.getBoxedType(this.getChildNodes().get(0).getType());
        Class typeTwo = JavaClassHelper.getBoxedType(this.getChildNodes().get(1).getType());

        if ((typeOne != String.class) || (typeTwo != String.class))
        {
            if (!JavaClassHelper.isNumeric(typeOne))
            {
                throw new ExprValidationException("Implicit conversion from datatype '" +
                        typeOne.getSimpleName() +
                        "' to numeric is not allowed");
            }
            if (!JavaClassHelper.isNumeric(typeTwo))
            {
                throw new ExprValidationException("Implicit conversion from datatype '" +
                        typeTwo.getSimpleName() +
                        "' to numeric is not allowed");
            }
        }

        Class compareType = JavaClassHelper.getCompareToCoercionType(typeOne, typeTwo);

        computer = relationalOpEnum.getComputer(compareType, typeOne, typeTwo);
    }

    public Class getType()
    {
        return Boolean.class;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        Object valueLeft = this.getChildNodes().get(0).evaluate(eventsPerStream, isNewData);
        if (valueLeft == null)
        {
            return null;
        }
        
        Object valueRight = this.getChildNodes().get(1).evaluate(eventsPerStream, isNewData);
        if (valueRight == null)
        {
            return null;
        }

        return computer.compare(valueLeft, valueRight);
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append(this.getChildNodes().get(0).toExpressionString());
        buffer.append(relationalOpEnum.getExpressionText());
        buffer.append(this.getChildNodes().get(1).toExpressionString());

        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprRelationalOpNode))
        {
            return false;
        }

        ExprRelationalOpNode other = (ExprRelationalOpNode) node;

        if (other.relationalOpEnum != this.relationalOpEnum)
        {
            return false;
        }

        return true;
    }
}
