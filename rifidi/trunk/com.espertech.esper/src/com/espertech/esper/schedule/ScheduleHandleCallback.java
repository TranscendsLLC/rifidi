/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.schedule;

import com.espertech.esper.core.ExtensionServicesContext;

/**
 * Interface for scheduled callbacks.
 */
public interface ScheduleHandleCallback 
{
    /**
     * Callback that is invoked as indicated by a schedule added to the scheduling service.
     * @param extensionServicesContext is a marker interface for providing custom extension services
     * passed to the triggered class
     */
    public void scheduledTrigger(ExtensionServicesContext extensionServicesContext);
}
