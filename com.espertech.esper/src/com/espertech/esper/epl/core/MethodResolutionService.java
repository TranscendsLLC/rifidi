/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.type.MinMaxTypeEnum;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.epl.agg.AggregationMethod;
import com.espertech.esper.epl.agg.AggregationSupport;

import java.lang.reflect.Method;

/**
 * Service for resolving methods and aggregation functions, and for creating managing aggregation instances.
 */
public interface MethodResolutionService
{
    /**
     * Returns true to cache UDF results for constant parameter sets.
     * @return cache UDF results config
     */
    public boolean isUdfCache();

    /**
     * Resolves a given method name and list of parameter types to an instance or static method exposed by the given class.
     * @param clazz is the class to look for a fitting method
     * @param methodName is the method name
     * @param paramTypes is parameter types match expression sub-nodes
     * @return method this resolves to
     * @throws EngineImportException if the method cannot be resolved to a visible static or instance method
     */
    public Method resolveMethod(Class clazz, String methodName, Class[] paramTypes) throws EngineImportException;

    /**
     * Resolves a given class, method and list of parameter types to a static method.
     * @param className is the class name to use
     * @param methodName is the method name
     * @param paramTypes is parameter types match expression sub-nodes
     * @return method this resolves to
     * @throws EngineImportException if the method cannot be resolved to a visible static method
     */
    public Method resolveMethod(String className, String methodName, Class[] paramTypes) throws EngineImportException;

    /**
     * Resolves a given class and method name to a static method, not allowing overloaded methods
     * and expecting the method to be found exactly once with zero or more parameters.
     * @param className is the class name to use
     * @param methodName is the method name
     * @return method this resolves to
     * @throws EngineImportException if the method cannot be resolved to a visible static method, or if the method exists more
     * then once with different parameters
     */
    public Method resolveMethod(String className, String methodName) throws EngineImportException;

    /**
     * Resolves a given class name, either fully qualified and simple and imported to a class.
     * @param className is the class name to use
     * @return class this resolves to
     * @throws EngineImportException if there was an error resolving the class
     */
    public Class resolveClass(String className) throws EngineImportException;

    /**
     * Returns a plug-in aggregation method for a given configured aggregation function name.
     * @param functionName is the aggregation function name
     * @return aggregation-providing class
     * @throws EngineImportUndefinedException is the function name cannot be found
     * @throws EngineImportException if there was an error resolving class information
     */
    public AggregationSupport resolveAggregation(String functionName) throws EngineImportUndefinedException, EngineImportException;

    /**
     * Makes a new plug-in aggregation instance by name.
     * @param name is the plug-in aggregation function name
     * @return new instance of plug-in aggregation method
     */
    public AggregationSupport makePlugInAggregator(String name);

    /**
     * Makes a new count-aggregator.
     * @param isIgnoreNull is true to ignore nulls, or false to count nulls
     * @return aggregator
     */
    public AggregationMethod makeCountAggregator(boolean isIgnoreNull);

    /**
     * Makes a new sum-aggregator.
     * @param type is the type to be summed up, i.e. float, long etc.
     * @return aggregator
     */
    public AggregationMethod makeSumAggregator(Class type);

    /**
     * Makes a new distinct-value-aggregator.
     * @param aggregationMethod is the inner aggregation method
     * @param childType is the return type of the inner expression to aggregate, if any
     * @return aggregator
     */
    public AggregationMethod makeDistinctAggregator(AggregationMethod aggregationMethod, Class childType);

    /**
     * Makes a new avg-aggregator.
     * @param type the expression return type
     * @return aggregator
     */
    public AggregationMethod makeAvgAggregator(Class type);

    /**
     * Makes a new avedev-aggregator.
     * @return aggregator
     */
    public AggregationMethod makeAvedevAggregator();

    /**
     * Makes a new median-aggregator.
     * @return aggregator
     */
    public AggregationMethod makeMedianAggregator();

    /**
     * Makes a new min-max-aggregator.
     * @param minMaxType dedicates whether to do min or max
     * @param targetType is the type to max or min
     * @return aggregator
     */
    public AggregationMethod makeMinMaxAggregator(MinMaxTypeEnum minMaxType, Class targetType);

    /**
     * Makes a new stddev-aggregator.
     * @return aggregator
     */
    public AggregationMethod makeStddevAggregator();

    /**
     * Returns a new set of aggregators given an existing prototype-set of aggregators for a given group key.
     * @param prototypes is the prototypes
     * @param groupKey is the key to group-by for
     * @return new set of aggregators for this group
     */
    public AggregationMethod[] newAggregators(AggregationMethod[] prototypes, MultiKeyUntyped groupKey);
}
