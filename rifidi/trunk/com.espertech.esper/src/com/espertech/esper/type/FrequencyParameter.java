/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.type;

import java.util.Set;
import java.util.HashSet;
import java.io.StringWriter;

/**
 * Encapsulates a parameter specifying a frequency, i.e. '* / 5'.
 */
public class FrequencyParameter implements NumberSetParameter
{
    private int frequency;

    /**
     * Ctor.
     * @param frequency - divisor specifying frequency
     */
    public FrequencyParameter(int frequency)
    {
        this.frequency = frequency;

        if (frequency <= 0)
        {
            throw new IllegalArgumentException("Zero or negative value supplied as freqeuncy");
        }
    }

    /**
     * Returns frequency.
     * @return frequency divisor
     */
    public int getFrequency()
    {
        return frequency;
    }

    public boolean isWildcard(int min, int max)
    {
        if (frequency == 1)
        {
            return true;
        }
        return false;
    }

    public Set<Integer> getValuesInRange(int min, int max)
    {
        Set<Integer> values = new HashSet<Integer>();
        int start = min - min % frequency;

        do
        {
            if (start >= min)
            {
                values.add(start);
            }
            start += frequency;
        }
        while (start <= max);

        return values;
    }
}
