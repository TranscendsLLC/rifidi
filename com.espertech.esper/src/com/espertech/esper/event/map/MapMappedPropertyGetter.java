/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.map;

import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.PropertyAccessException;

import java.util.Map;

/**
 * Getter for a dynamic mappeds property for maps.
 */
public class MapMappedPropertyGetter implements EventPropertyGetter
{
    private final String key;
    private final String fieldName;

    /**
     * Ctor.
     * @param fieldName property name
     * @param key get the element at
     */
    public MapMappedPropertyGetter(String fieldName, String key)
    {
        this.key = key;
        this.fieldName = fieldName;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException
    {
        Object underlying = eventBean.getUnderlying();
        if (!(underlying instanceof Map))
        {
            return null;
        }
        Map map = (Map) underlying;
        Object value = map.get(fieldName);
        if (value == null)
        {
            return null;
        }
        if (!(value instanceof Map))
        {
            return null;
        }
        Map innerMap = (Map) value;
        return innerMap.get(key);
    }

    public boolean isExistsProperty(EventBean eventBean)
    {
        Object underlying = eventBean.getUnderlying();
        if (!(underlying instanceof Map))
        {
            return false;
        }
        Map map = (Map) underlying;
        Object value = map.get(fieldName);
        if (value == null)
        {
            return false;
        }
        if (!(value instanceof Map))
        {
            return false;
        }
        Map innerMap = (Map) value;
        return innerMap.containsKey(key);
    }

    public Object getFragment(EventBean eventBean)
    {
        return null;
    }
}
