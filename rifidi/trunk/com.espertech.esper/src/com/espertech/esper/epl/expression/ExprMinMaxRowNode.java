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
import com.espertech.esper.util.SimpleNumberBigDecimalCoercer;
import com.espertech.esper.util.SimpleNumberBigIntegerCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;
import com.espertech.esper.type.*;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * Represents the MAX(a,b) and MIN(a,b) functions is an expression tree.
 */
public class ExprMinMaxRowNode extends ExprNode
{
    private MinMaxTypeEnum minMaxTypeEnum;
    private Class resultType;
    private MinMaxTypeEnum.Computer computer;

    /**
     * Ctor.
     * @param minMaxTypeEnum - type of compare
     */
    public ExprMinMaxRowNode(MinMaxTypeEnum minMaxTypeEnum)
    {
        this.minMaxTypeEnum = minMaxTypeEnum;
    }

    /**
     * Returns the indicator for minimum or maximum.
     * @return min/max indicator
     */
    public MinMaxTypeEnum getMinMaxTypeEnum()
    {
        return minMaxTypeEnum;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        if (this.getChildNodes().size() < 2)
        {
            throw new ExprValidationException("MinMax node must have at least 2 child nodes");
        }

        for (ExprNode child : this.getChildNodes())
        {
            Class childType = child.getType();
            if (!JavaClassHelper.isNumeric(childType))
            {
                throw new ExprValidationException("Implicit conversion from datatype '" +
                        childType.getSimpleName() +
                        "' to numeric is not allowed");
            }
        }

        // Determine result type, set up compute function
        Class childTypeOne = this.getChildNodes().get(0).getType();
        Class childTypeTwo = this.getChildNodes().get(1).getType();
        resultType = JavaClassHelper.getArithmaticCoercionType(childTypeOne, childTypeTwo);

        for (int i = 2; i < this.getChildNodes().size(); i++)
        {
            resultType = JavaClassHelper.getArithmaticCoercionType(resultType, this.getChildNodes().get(i).getType());
        }

        ExprNode[] childNodes = this.getChildNodes().toArray(new ExprNode[this.getChildNodes().size()]);
        if (resultType == BigInteger.class)
        {
            SimpleNumberBigIntegerCoercer[] convertors = new SimpleNumberBigIntegerCoercer[childNodes.length];
            for (int i = 0; i < childNodes.length; i++)
            {
                convertors[i] = SimpleNumberCoercerFactory.getCoercerBigInteger(childNodes[i].getType());
            }
            computer = new MinMaxTypeEnum.ComputerBigIntCoerce(childNodes, convertors, (minMaxTypeEnum == MinMaxTypeEnum.MAX));
        }
        else if (resultType == BigDecimal.class)
        {
            SimpleNumberBigDecimalCoercer[] convertors = new SimpleNumberBigDecimalCoercer[childNodes.length];
            for (int i = 0; i < childNodes.length; i++)
            {
                convertors[i] = SimpleNumberCoercerFactory.getCoercerBigDecimal(childNodes[i].getType());
            }
            computer = new MinMaxTypeEnum.ComputerBigDecCoerce(childNodes, convertors, (minMaxTypeEnum == MinMaxTypeEnum.MAX));
        }
        else {
            if (minMaxTypeEnum == MinMaxTypeEnum.MAX)
            {
                computer = new MinMaxTypeEnum.MaxComputerDoubleCoerce(childNodes);
            }
            else
            {
                computer = new MinMaxTypeEnum.MinComputerDoubleCoerce(childNodes);
            }                
        }
    }

    public Class getType()
    {
        return resultType;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        Number result = computer.execute(eventsPerStream, isNewData);
        if (result == null)
        {
            return null;
        }
        return JavaClassHelper.coerceBoxed(result, resultType);
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(minMaxTypeEnum.getExpressionText());
        buffer.append('(');

        buffer.append(this.getChildNodes().get(0).toExpressionString());
        buffer.append(',');
        buffer.append(this.getChildNodes().get(1).toExpressionString());

        for (int i = 2; i < this.getChildNodes().size(); i++)
        {
            buffer.append(',');
            buffer.append(this.getChildNodes().get(i).toExpressionString());
        }

        buffer.append(')');
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprMinMaxRowNode))
        {
            return false;
        }

        ExprMinMaxRowNode other = (ExprMinMaxRowNode) node;

        if (other.minMaxTypeEnum != this.minMaxTypeEnum)
        {
            return false;
        }

        return true;
    }
}
