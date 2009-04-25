/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg;

import com.espertech.esper.epl.core.MethodResolutionService;

/**
 * Base class for use with plug-in aggregation functions.
 */
public abstract class AggregationSupport implements AggregationMethod
{
    /**
     * Provides the aggregation function name.
     */
    protected String functionName;

    /**
     * Implemented by plug-in aggregation functions to allow such functions to validate the
     * type of value passed to the function at statement compile time.
     * @param childNodeType is the class of result of the expression sub-node within the aggregation function, or
     * null if a statement supplies no expression within the aggregation function
     */
    public abstract void validate(Class childNodeType);

    /**
     * Override this method to validate multiple parameters to a plug-in aggregation function.
     * <p>
     * Any parameters to the aggregation function that are constant-value expressions are indicated as a boolean true
     * and the value of the constant-value expression.
     * @param parameterType parameter type
     * @param isConstantValue true to indicate the parameter is a constant or a result of an expression that is constant
     * @param constantValue the value of the constant-value expression, or null if not a constant-value expression
     */
    public void validateMultiParameter(Class[] parameterType, boolean[] isConstantValue, Object[] constantValue)
    {
        // no implementation, this method may be overridden by the implementation if the method accepts multiple parameters
        // and wishes to validate or keep a reference to constants
    }

    /**
     * Ctor.
     */
    public AggregationSupport()
    {
    }

    /**
     * Sets the aggregation function name.
     * @param functionName is the name of the aggregation function
     */
    public void setFunctionName(String functionName)
    {
        this.functionName = functionName;
    }

    /**
     * Returns the name of the aggregation function.
     * @return aggregation function name
     */
    public String getFunctionName()
    {
        return functionName;
    }

    public AggregationMethod newAggregator(MethodResolutionService methodResolutionService)
    {
        return methodResolutionService.makePlugInAggregator(functionName);
    }
}
