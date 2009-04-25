/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.EventBean;

/**
 * Interface for a service that routes events within the engine for further processing.
 */
public interface InternalEventRouter
{
    /**
     * Route the event such that the event is processed as required.
     * @param event to route
     * @param statementHandle provides statement resources
     */
    public void route(EventBean event, EPStatementHandle statementHandle);
}
