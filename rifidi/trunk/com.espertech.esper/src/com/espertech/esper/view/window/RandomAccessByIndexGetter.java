/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

/**
 * Getter that provides an index at runtime.
 */
public class RandomAccessByIndexGetter implements RandomAccessByIndexObserver
{
    private RandomAccessByIndex randomAccessByIndex;

    /**
     * Returns the index for access.
     * @return index
     */
    public RandomAccessByIndex getAccessor()
    {
        return randomAccessByIndex;
    }

    public void updated(RandomAccessByIndex randomAccessByIndex)
    {
        this.randomAccessByIndex = randomAccessByIndex;
    }
}
