/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.schedule;

import com.espertech.esper.timer.TimeSourceService;

/**
 * Static factory for implementations of the SchedulingService interface.
 */
public final class SchedulingServiceProvider
{
    /**
     * Creates an implementation of the SchedulingService interface.
     * @param timeSourceService time source provider
     * @return implementation
     */
    public static SchedulingService newService(TimeSourceService timeSourceService)
    {
        return new SchedulingServiceImpl(timeSourceService);
    }
}
