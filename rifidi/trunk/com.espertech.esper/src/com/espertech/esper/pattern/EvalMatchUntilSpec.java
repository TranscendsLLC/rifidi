/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.util.MetaDefItem;

import java.io.Serializable;

/**
 * Specification for a range for the pattern-repeat operator.
 */
public class EvalMatchUntilSpec implements MetaDefItem, Serializable
{
    private final Integer lowerBounds;
    private final Integer upperBounds;
    private final boolean hasBounds;
    private final boolean isTightlyBound;
    private static final long serialVersionUID = -78885747872100470L;

    /**
     * Ctor.
     * @param lowerBounds is the lower bounds, or null if none supplied
     * @param upperBounds is the upper bounds, or null if none supplied
     */
    public EvalMatchUntilSpec(Integer lowerBounds, Integer upperBounds)
    {
        if ((lowerBounds != null) && (lowerBounds < 0))
        {
            throw new IllegalArgumentException("Lower bounds in match-until cannot be a negative value");
        }
        if ((upperBounds != null) && (upperBounds < 0))
        {
            throw new IllegalArgumentException("Upper bounds in match-until cannot be a negative value");
        }

        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;

        if ((lowerBounds != null) || (upperBounds != null))
        {
            hasBounds = true;
        }
        else
        {
            hasBounds = false;
        }

        if ((lowerBounds != null) && (upperBounds != null))
        {
            if (lowerBounds > upperBounds)
            {
                throw new IllegalArgumentException("Lower bounds in match-until cannot be greater then the upper bounds");
            }

            if (lowerBounds.equals(upperBounds))
            {
                isTightlyBound = true;
            }
            else
            {
                isTightlyBound = false;
            }
        }
        else
        {
            isTightlyBound = false;
        }
    }

    /**
     * Returns true if there is any endpoint, either low or high. Returns false for no endpoint.
     * @return true for has endpoint
     */
    public boolean isBounded()
    {
        return hasBounds;
    }

    /**
     * Returns true if there is a tight bounds, that is low and high endpoints are both defined.
     * @return true for tight endpoint.
     */
    public boolean isTightlyBound()
    {
        return isTightlyBound;
    }

    /**
     * Returns the lower endpoint or null if undefined.
     * @return lower endpoint
     */
    public Integer getLowerBounds()
    {
        return lowerBounds;
    }

    /**
     * Returns the high endpoint or null if undefined.
     * @return high endpoint
     */
    public Integer getUpperBounds()
    {
        return upperBounds;
    }
}
