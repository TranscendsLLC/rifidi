/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.bean;

import com.espertech.esper.client.Configuration;

/**
 * Interface for a factory for obtaining {@link BeanEventType} instances.
 */
public interface BeanEventTypeFactory
{
    /**
     * Returns the bean event type for a given class assigning the given name.
     * @param name is the name
     * @param clazz is the class for which to generate an event type
     * @param isConfigured if the class is a configuration value
     * @return is the event type for the class
     */
    public BeanEventType createBeanType(String name, Class clazz, boolean isConfigured);

    /**
     * Returns the bean event type for a given class assigning the given name.
     * @param clazz is the class for which to generate an event type
     * @return is the event type for the class
     */
    public BeanEventType createBeanTypeDefaultName(Class clazz);

    /**
     * Returns the default property resolution style.
     * @return property resolution style
     */
    public Configuration.PropertyResolutionStyle getDefaultPropertyResolutionStyle();
}
