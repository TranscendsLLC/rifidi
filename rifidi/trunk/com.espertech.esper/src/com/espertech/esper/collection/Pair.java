/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.collection;

import java.io.Serializable;

/**
 * General-purpose pair of values of any type. The pair only equals another pair if
 * the objects that form the pair equal, ie. first pair first object equals (.equals) the second pair first object,
 * and the first pair second object equals the second pair second object.
 */
public final class Pair<First,Second> implements Serializable
{
    private First first;
    private Second second;
    private static final long serialVersionUID = -4168417618011472714L;

    /**
     * Construct pair of values.
     * @param first is the first value
     * @param second is the second value
     */
    public Pair(final First first, final Second second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns first value within pair.
     * @return first value within pair
     */
    public First getFirst()
    {
        return first;
    }

    /**
     * Returns second value within pair.
     * @return second value within pair
     */
    public Second getSecond()
    {
        return second;
    }

    /**
     * Set the first value of the pair to a new value.
     * @param first value to be set
     */
    public void setFirst(First first)
    {
        this.first = first;
    }

    /**
     * Set the second value of the pair to a new value.
     * @param second value to be set
     */
    public void setSecond(Second second)
    {
        this.second = second;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Pair))
        {
            return false;
        }

        Pair other = (Pair) obj;

        return  (first == null ?
                other.first == null : first.equals(other.first)) &&
                (second == null ?
                other.second == null : second.equals(other.second));
    }

    public int hashCode()
    {
        return (first == null ? 0 : first.hashCode()) ^
                (second == null ? 0 : second.hashCode());
    }

    public String toString()
    {
        return "Pair [" + first + ':' + second + ']';
    }
}
