/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.bean;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.util.JavaClassHelper;

import java.lang.reflect.Field;

/**
 * Property getter for fields using Java's vanilla reflection.
 */
public final class ReflectionPropFieldGetter extends BaseNativePropertyGetter implements BeanEventPropertyGetter
{
    private final Field field;

    /**
     * Constructor.
     * @param field is the regular reflection field to use to obtain values for a property
     * @param eventAdapterService factory for event beans and event types
     */
    public ReflectionPropFieldGetter(Field field, EventAdapterService eventAdapterService)
    {
        super(eventAdapterService, field.getType(), JavaClassHelper.getGenericFieldType(field, true));
        this.field = field;
    }

    public Object getBeanProp(Object object) throws PropertyAccessException
    {
        try
        {
            return field.get(object);
        }
        catch (IllegalArgumentException e)
        {
            throw new PropertyAccessException("Mismatched getter instance to event bean type");
        }
        catch (IllegalAccessException e)
        {
            throw new PropertyAccessException(e);
        }
    }

    public boolean isBeanExistsProperty(Object object)
    {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public final Object get(EventBean obj) throws PropertyAccessException
    {
        Object underlying = obj.getUnderlying();
        return getBeanProp(underlying);
    }

    public String toString()
    {
        return "ReflectionPropFieldGetter " +
                "field=" + field.toGenericString();
    }

    public boolean isExistsProperty(EventBean eventBean)
    {
        return true; // Property exists as the property is not dynamic (unchecked)
    }
}
