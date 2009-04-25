/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event;

import com.espertech.esper.client.EventType;

/**
 * Service provider interface for internal use for event types.
 */
public interface EventTypeSPI extends EventType
{
    /**
     * Returns the type metadata.
     * @return type metadata
     */
    public EventTypeMetadata getMetadata();
}