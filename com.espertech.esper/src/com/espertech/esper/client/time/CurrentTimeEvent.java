/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.time;

import java.util.Date;

/**
 * Event for externally controlling the time within an {@link com.espertech.esper.client.EPRuntime} instance.
 * External clocking must be enabled via {@link TimerControlEvent} before this class can be used
 * to externally feed time.
 */
public final class CurrentTimeEvent extends TimerEvent
{
    private final long timeInMillis;

    /**
     * Constructor.
     * @param timeInMillis is the time in milliseconds
     */
    public CurrentTimeEvent(final long timeInMillis)
    {
        this.timeInMillis = timeInMillis;
    }

    public String toString()
    {
        return (new Date(timeInMillis)).toString();
    }

    /**
     * Returns the time in milliseconds.
     * @return time in milliseconds
     */
    public long getTimeInMillis()
    {
        return timeInMillis;
    }
}
