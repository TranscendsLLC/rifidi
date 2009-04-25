/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.epl.agg.*;
import com.espertech.esper.type.MinMaxTypeEnum;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.client.EPException;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * Implements method resolution.
 */
public class MethodResolutionServiceImpl implements MethodResolutionService
{
	private final EngineImportService engineImportService;
    private final boolean isUdfCache;

    /**
     * Ctor.
     * @param engineImportService is the engine imports
     * @param isUdfCache returns true to cache UDF results for constant parameter sets
     */
    public MethodResolutionServiceImpl(EngineImportService engineImportService,
                                       boolean isUdfCache)
	{
        this.engineImportService = engineImportService;
        this.isUdfCache = isUdfCache;
    }

    public boolean isUdfCache()
    {
        return isUdfCache;
    }

    public AggregationSupport makePlugInAggregator(String functionName)
    {
        try
        {
            return engineImportService.resolveAggregation(functionName);
        }
        catch (EngineImportUndefinedException e)
        {
            throw new EPException("Failed to make new aggregation function instance for '" + functionName + "'", e);
        }
        catch (EngineImportException e)
        {
            throw new EPException("Failed to make new aggregation function instance for '" + functionName + "'", e);
        }
    }

    public Method resolveMethod(String className, String methodName, Class[] paramTypes)
			throws EngineImportException
    {
        return engineImportService.resolveMethod(className, methodName, paramTypes);
	}

    public Method resolveMethod(String className, String methodName)
			throws EngineImportException
    {
        return engineImportService.resolveMethod(className, methodName);
	}

    public Class resolveClass(String className)
			throws EngineImportException
    {
        return engineImportService.resolveClass(className);
	}

    public Method resolveMethod(Class clazz, String methodName, Class[] paramTypes) throws EngineImportException
    {
        return engineImportService.resolveMethod(clazz, methodName, paramTypes);
    }

    public AggregationMethod makeCountAggregator(boolean isIgnoreNull)
    {
        if (isIgnoreNull)
        {
            return new NonNullCountAggregator();
        }
        return new CountAggregator();
    }

    public AggregationSupport resolveAggregation(String functionName) throws EngineImportUndefinedException, EngineImportException
    {
        return engineImportService.resolveAggregation(functionName);
    }

    public AggregationMethod makeSumAggregator(Class type)
    {
        if (type == BigInteger.class)
        {
            return new BigIntegerSumAggregator();
        }
        if (type == BigDecimal.class)
        {
            return new BigDecimalSumAggregator();
        }
        if ((type == Long.class) || (type == long.class))
        {
            return new LongSumAggregator();
        }
        if ((type == Integer.class) || (type == int.class))
        {
            return new IntegerSumAggregator();
        }
        if ((type == Double.class) || (type == double.class))
        {
            return new DoubleSumAggregator();
        }
        if ((type == Float.class) || (type == float.class))
        {
            return new FloatSumAggregator();
        }
        return new NumIntegerSumAggregator();
    }

    public AggregationMethod makeDistinctAggregator(AggregationMethod aggregationMethod, Class childType)
    {
        return new DistinctValueAggregator(aggregationMethod, childType);
    }

    public AggregationMethod makeAvgAggregator(Class type)
    {
        if ((type == BigDecimal.class) || (type == BigInteger.class))
        {
            return new BigDecimalAvgAggregator();
        }
        return new AvgAggregator();
    }

    public AggregationMethod makeAvedevAggregator()
    {
        return new AvedevAggregator();
    }

    public AggregationMethod makeMedianAggregator()
    {
        return new MedianAggregator();
    }

    public AggregationMethod makeMinMaxAggregator(MinMaxTypeEnum minMaxTypeEnum, Class targetType)
    {
        return new MinMaxAggregator(minMaxTypeEnum, targetType);
    }

    public AggregationMethod makeStddevAggregator()
    {
        return new StddevAggregator();
    }

    public AggregationMethod[] newAggregators(AggregationMethod[] prototypes, MultiKeyUntyped groupKey)
    {
        AggregationMethod row[] = new AggregationMethod[prototypes.length];
        for (int i = 0; i < prototypes.length; i++)
        {
            row[i] = prototypes[i].newAggregator(this);
        }
        return row;
    }
}
