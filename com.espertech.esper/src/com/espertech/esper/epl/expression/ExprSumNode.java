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
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;

/**
 * Represents the sum(...) aggregate function is an expression tree.
 */
public class ExprSumNode extends ExprAggregateNode
{
    /**
     * Ctor.
     * @param distinct - flag indicating unique or non-unique value aggregation
     */
    public ExprSumNode(boolean distinct)
    {
        super(distinct);
    }

    public AggregationMethod validateAggregationChild(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService) throws ExprValidationException
    {
        Class childType = super.validateSingleNumericChild(streamTypeService);
        return methodResolutionService.makeSumAggregator(childType);
    }

    protected String getAggregationFunctionName()
    {
        return "sum";
    }

    public final boolean equalsNodeAggregate(ExprAggregateNode node)
    {
        if (!(node instanceof ExprSumNode))
        {
            return false;
        }

        return true;
    }
}
