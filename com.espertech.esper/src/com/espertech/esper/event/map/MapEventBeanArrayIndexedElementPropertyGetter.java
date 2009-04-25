package com.espertech.esper.event.map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.PropertyAccessException;

import java.util.Map;

/**
 * Getter for an array of event bean using a nested getter.
 */
public class MapEventBeanArrayIndexedElementPropertyGetter implements EventPropertyGetter
{
    private final String propertyName;
    private final int index;
    private final EventPropertyGetter nestedGetter;

    /**
     * Ctor.
     * @param propertyName property name
     * @param index array index
     * @param nestedGetter nested getter
     */
    public MapEventBeanArrayIndexedElementPropertyGetter(String propertyName, int index, EventPropertyGetter nestedGetter)
    {
        this.propertyName = propertyName;
        this.index = index;
        this.nestedGetter = nestedGetter;
    }

    public Object get(EventBean obj)
    {
        // The underlying is expected to be a map
        if (!(obj.getUnderlying() instanceof Map))
        {
            throw new PropertyAccessException("Mismatched property getter to event bean type, " +
                    "the underlying data object is not of type java.lang.Map");
        }

        Map map = (Map) obj.getUnderlying();

        // If the map does not contain the key, this is allowed and represented as null
        EventBean[] wrapper = (EventBean[]) map.get(propertyName);

        if (wrapper == null)
        {
            return null;
        }
        if (wrapper.length <= index)
        {
            return null;
        }
        EventBean innerArrayEvent = wrapper[index];
        return nestedGetter.get(innerArrayEvent);
    }

    public boolean isExistsProperty(EventBean eventBean)
    {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Object getFragment(EventBean obj)
    {
        // The underlying is expected to be a map
        if (!(obj.getUnderlying() instanceof Map))
        {
            throw new PropertyAccessException("Mismatched property getter to event bean type, " +
                    "the underlying data object is not of type java.lang.Map");
        }

        Map map = (Map) obj.getUnderlying();

        // If the map does not contain the key, this is allowed and represented as null
        EventBean[] wrapper = (EventBean[]) map.get(propertyName);

        if (wrapper == null)
        {
            return null;
        }
        if (wrapper.length <= index)
        {
            return null;
        }
        EventBean innerArrayEvent = wrapper[index];
        return nestedGetter.getFragment(innerArrayEvent);
    }
}