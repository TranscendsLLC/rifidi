/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.vaevent;

import com.espertech.esper.client.EventType;
import com.espertech.esper.client.ConfigurationVariantStream;

/**
 * Specification for a variant event stream.
 */
public class VariantSpec
{
    private final String variantStreamName;
    private final EventType[] eventTypes;
    private final ConfigurationVariantStream.TypeVariance typeVariance;

    /**
     * Ctor.
     * @param variantStreamName name of variant stream
     * @param eventTypes types of events for variant stream, or empty list
     * @param typeVariance enum specifying type variance
     */
    public VariantSpec(String variantStreamName, EventType[] eventTypes, ConfigurationVariantStream.TypeVariance typeVariance)
    {
        this.variantStreamName = variantStreamName;
        this.eventTypes = eventTypes;
        this.typeVariance = typeVariance;
    }

    /**
     * Returns name of variant stream.
     * @return name
     */
    public String getVariantStreamName()
    {
        return variantStreamName;
    }

    /**
     * Returns types allowed for variant streams.
     * @return types
     */
    public EventType[] getEventTypes()
    {
        return eventTypes;
    }

    /**
     * Returns the type variance enum.
     * @return type variance
     */
    public ConfigurationVariantStream.TypeVariance getTypeVariance()
    {
        return typeVariance;
    }
}
