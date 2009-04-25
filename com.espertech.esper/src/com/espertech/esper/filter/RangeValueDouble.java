/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.pattern.MatchedEventMap;

/**
 * A Double-typed value as a filter parameter representing a range.
 */
public class RangeValueDouble implements FilterSpecParamRangeValue
{
    private final double doubleValue;

    /**
     * Ctor.
     * @param doubleValue is the value of the range endpoint
     */
    public RangeValueDouble(double doubleValue)
    {
        this.doubleValue = doubleValue;
    }

    public final Double getFilterValue(MatchedEventMap matchedEvents)
    {
        return doubleValue;
    }

    public int getFilterHash()
    {
        return Double.valueOf(doubleValue).hashCode();
    }

    /**
     * Returns the constant value.
     * @return constant
     */
    public double getDoubleValue()
    {
        return doubleValue;
    }

    public final String toString()
    {
        return Double.toString(doubleValue);
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof RangeValueDouble))
        {
            return false;
        }

        RangeValueDouble other = (RangeValueDouble) obj;
        return other.doubleValue == this.doubleValue;
    }

    public int hashCode()
    {
        long temp = doubleValue != +0.0d ? Double.doubleToLongBits(doubleValue) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
}
