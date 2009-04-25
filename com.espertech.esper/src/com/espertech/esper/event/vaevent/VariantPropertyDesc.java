/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.vaevent;

import com.espertech.esper.client.EventPropertyGetter;

/**
 * Descriptor for a variant stream property.
 */
public class VariantPropertyDesc
{
    private final Class propertyType;
    private final EventPropertyGetter getter;
    private final boolean isProperty;

    /**
     * Ctor.
     * @param propertyType type or null if not exists
     * @param getter the getter or null if not exists
     * @param property the boolean indicating whether it exists or not
     */
    public VariantPropertyDesc(Class propertyType, EventPropertyGetter getter, boolean property)
    {
        this.propertyType = propertyType;
        this.getter = getter;
        isProperty = property;
    }

    /**
     * True if the property exists, false if not.
     * @return indicator whether property exists
     */
    public boolean isProperty()
    {
        return isProperty;
    }

    /**
     * Returns the property type.
     * @return property type
     */
    public Class getPropertyType()
    {
        return propertyType;
    }

    /**
     * Returns the getter for the property.
     * @return property getter
     */
    public EventPropertyGetter getGetter()
    {
        return getter;
    }
}
