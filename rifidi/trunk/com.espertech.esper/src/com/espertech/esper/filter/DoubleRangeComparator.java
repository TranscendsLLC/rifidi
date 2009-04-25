/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.util.MetaDefItem;

import java.util.Comparator;
import java.io.Serializable;

/**
 * Comparator for DoubleRange values.
 * <p>Sorts double ranges as this:     sort by min asc, max asc.
 * I.e. same minimum value sorts maximum value ascending.
 */
public final class DoubleRangeComparator implements Comparator<DoubleRange>, MetaDefItem, Serializable
{
    private static final long serialVersionUID = 5806978023193814183L;

    public final int compare(DoubleRange r1, DoubleRange r2)
    {
        double minOne = r1.getMin();
        double minTwo = r2.getMin();
        double maxOne = r1.getMax();
        double maxTwo = r2.getMax();

        if (minOne < minTwo)
        {
            return -1;
        }
        if (minOne > minTwo)
        {
            return 1;
        }
        if (maxOne < maxTwo)
        {
            return -1;
        }
        if (maxOne > maxTwo)
        {
            return 1;
        }

        return 0;
    }
}
