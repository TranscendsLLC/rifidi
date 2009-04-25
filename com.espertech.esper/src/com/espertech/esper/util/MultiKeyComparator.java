/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.util;

import com.espertech.esper.collection.MultiKeyUntyped;

import java.util.Comparator;
import java.text.Collator;
import java.io.Serializable;

/**
 * A comparator on multikeys. The multikeys must contain the same
 * number of values.
 */
public final class MultiKeyComparator implements Comparator<MultiKeyUntyped>, MetaDefItem, Serializable
{
    private final boolean[] isDescendingValues;
    private static final long serialVersionUID = -5990983090238885417L;

    /**
     * Ctor.
     * @param isDescendingValues - each value is true if the corresponding (same index)
     *        entry in the multi-keys is to be sorted in descending order. The multikeys
     *        to be compared must have the same number of values as this array.
     */
    public MultiKeyComparator(boolean[] isDescendingValues)
    {
        this.isDescendingValues = isDescendingValues;
    }

    public final int compare(MultiKeyUntyped firstValues, MultiKeyUntyped secondValues)
    {
        if(firstValues.size() != isDescendingValues.length || secondValues.size() != isDescendingValues.length)
        {
            throw new IllegalArgumentException("Incompatible size MultiKey sizes for comparison");
        }

        for (int i = 0; i < firstValues.size(); i++)
        {
            Object valueOne = firstValues.get(i);
            Object valueTwo = secondValues.get(i);
            boolean isDescending = isDescendingValues[i];

            int comparisonResult = compareValues(valueOne, valueTwo, isDescending);
            if (comparisonResult != 0)
            {
                return comparisonResult;
            }
        }

        // Make the comparator compatible with equals
        if(!firstValues.equals(secondValues))
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Compares two nullable values.
     * @param valueOne first value to compare
     * @param valueTwo second value to compare
     * @param isDescending true for descending
     * @return compare result
     */
    protected static int compareValues(Object valueOne, Object valueTwo, boolean isDescending)
    {
        if (valueOne == null || valueTwo == null)
        {
            // A null value is considered equal to another null
            // value and smaller than any nonnull value
            if (valueOne == null && valueTwo == null)
            {
                return 0;
            }
            if (valueOne == null)
            {
                if (isDescending)
                {
                    return 1;
                }
                return -1;
            }
            if (isDescending)
            {
                return -1;
            }
            return 1;
        }

        Comparable comparable1;
        if (valueOne instanceof Comparable)
        {
            comparable1 = (Comparable) valueOne;
        }
        else
        {
            throw new ClassCastException("Cannot sort objects of type " + valueOne.getClass());
        }

        if (isDescending)
        {
            return -1 * comparable1.compareTo(valueTwo);
        }

        return comparable1.compareTo(valueTwo);
    }

    /**
     * Compares two nullable values using Collator, for use with string-typed values.
     * @param valueOne first value to compare
     * @param valueTwo second value to compare
     * @param isDescending true for descending
     * @param collator the Collator for comparing
     * @return compare result
     */
    public static int compareValuesCollated(Object valueOne, Object valueTwo, boolean isDescending, Collator collator)
    {
        if (valueOne == null || valueTwo == null)
        {
            // A null value is considered equal to another null
            // value and smaller than any nonnull value
            if (valueOne == null && valueTwo == null)
            {
                return 0;
            }
            if (valueOne == null)
            {
                if (isDescending)
                {
                    return 1;
                }
                return -1;
            }
            if (isDescending)
            {
                return -1;
            }
            return 1;
        }

        if (isDescending)
        {
            return collator.compare(valueTwo, valueOne);
        }

        return collator.compare(valueOne, valueTwo);
    }
}
