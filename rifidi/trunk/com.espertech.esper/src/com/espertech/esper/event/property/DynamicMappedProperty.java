/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.property;

import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.event.bean.BeanEventType;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.map.MapMappedPropertyGetter;
import com.espertech.esper.event.bean.DynamicMappedPropertyGetter;
import com.espertech.esper.event.xml.SchemaElementComplex;
import com.espertech.esper.event.xml.SchemaItem;
import com.espertech.esper.event.xml.BaseXMLEventType;
import com.espertech.esper.event.xml.DOMMapGetter;

import java.util.Map;
import java.io.StringWriter;

/**
 * Represents a dynamic mapped property of a given name.
 * <p>
 * Dynamic properties always exist, have an Object type and are resolved to a method during runtime.
 */
public class DynamicMappedProperty extends PropertyBase implements DynamicProperty
{
    private final String key;

    /**
     * Ctor.
     * @param propertyName is the property name
     * @param key is the mapped access key
     */
    public DynamicMappedProperty(String propertyName, String key)
    {
        super(propertyName);
        this.key = key;
    }

    public boolean isDynamic()
    {
        return true;
    }

    public String[] toPropertyArray()
    {
        return new String[] {this.getPropertyNameAtomic()};
    }

    public EventPropertyGetter getGetter(BeanEventType eventType, EventAdapterService eventAdapterService)
    {
        return new DynamicMappedPropertyGetter(propertyNameAtomic, key, eventAdapterService);
    }

    public Class getPropertyType(BeanEventType eventType, EventAdapterService eventAdapterService)
    {
        return Object.class;
    }

    public GenericPropertyDesc getPropertyTypeGeneric(BeanEventType beanEventType, EventAdapterService eventAdapterService)
    {
        return GenericPropertyDesc.getObjectGeneric();
    }

    public Class getPropertyTypeMap(Map optionalMapPropTypes, EventAdapterService eventAdapterService)
    {
        return Object.class;
    }

    public EventPropertyGetter getGetterMap(Map optionalMapPropTypes, EventAdapterService eventAdapterService)
    {
        return new MapMappedPropertyGetter(this.getPropertyNameAtomic(), key);
    }

    public void toPropertyEPL(StringWriter writer)
    {
        writer.append(propertyNameAtomic);
        writer.append("('");
        writer.append(key);
        writer.append("')");
        writer.append('?');
    }

    public EventPropertyGetter getGetterDOM(SchemaElementComplex complexProperty, EventAdapterService eventAdapterService, BaseXMLEventType eventType, String propertyExpression)
    {
        return new DOMMapGetter(propertyNameAtomic, key, null);
    }

    public SchemaItem getPropertyTypeSchema(SchemaElementComplex complexProperty, EventAdapterService eventAdapterService)
    {
        return null;  // always returns Node
    }

    public EventPropertyGetter getGetterDOM()
    {
        return new DOMMapGetter(propertyNameAtomic, key, null);
    }
}
