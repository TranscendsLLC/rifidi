/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.bean;

import com.espertech.esper.event.bean.InternalEventPropDescriptor;

import java.util.List;

/**
 * Interface for an introspector that generates a list of event property descriptors
 * given a clazz. The clazz could be a JavaBean-style class or any other legacy type.
 */
public interface PropertyListBuilder
{
    /**
     * Introspect the clazz and deterime exposed event properties.
     * @param clazz to introspect
     * @return list of event property descriptors
     */
    public List<InternalEventPropDescriptor> assessProperties(Class clazz);
}
