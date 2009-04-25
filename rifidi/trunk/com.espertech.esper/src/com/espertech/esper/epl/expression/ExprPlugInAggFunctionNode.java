/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.agg.AggregationMethod;
import com.espertech.esper.epl.agg.AggregationSupport;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;

/**
 * Represents a custom aggregation function in an expresson tree.
 */
public class ExprPlugInAggFunctionNode extends ExprAggregateNode
{
    private AggregationSupport aggregationSupport;

    /**
     * Ctor.
     * @param distinct - flag indicating unique or non-unique value aggregation
     * @param aggregationSupport - is the base class for plug-in aggregation functions
     * @param functionName is the aggregation function name
     */
    public ExprPlugInAggFunctionNode(boolean distinct, AggregationSupport aggregationSupport, String functionName)
    {
        super(distinct);
        this.aggregationSupport = aggregationSupport;
        aggregationSupport.setFunctionName(functionName);
    }

    public AggregationMethod validateAggregationChild(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService) throws ExprValidationException
    {
        if (this.getChildNodes().size() > 1)
        {
            Class[] parameterTypes = new Class[this.getChildNodes().size()];
            Object[] constant = new Object[this.getChildNodes().size()];
            boolean[] isConstant = new boolean[this.getChildNodes().size()];

            int count = 0;
            for (ExprNode child : this.getChildNodes())
            {
                if (child.isConstantResult())
                {
                    isConstant[count] = true;
                    constant[count] = child.evaluate(null, true);
                }
                parameterTypes[count] = child.getType();
                count++;
            }
            aggregationSupport.validateMultiParameter(parameterTypes, isConstant, constant);
        }
        else if (this.getChildNodes().size() == 1)
        {
            Class childType = this.getChildNodes().get(0).getType();
            try
            {
                aggregationSupport.validate(childType);
            }
            catch (RuntimeException ex)
            {
                throw new ExprValidationException("Plug-in aggregation function '" + aggregationSupport.getFunctionName() + "' failed validation: " + ex.getMessage());
            }
        }

        return aggregationSupport;
    }

    public String getAggregationFunctionName()
    {
        return aggregationSupport.getFunctionName();
    }

    public final boolean equalsNodeAggregate(ExprAggregateNode node)
    {
        if (!(node instanceof ExprPlugInAggFunctionNode))
        {
            return false;
        }

        ExprPlugInAggFunctionNode other = (ExprPlugInAggFunctionNode) node;
        return other.getAggregationFunctionName().equals(this.getAggregationFunctionName());
    }
}
