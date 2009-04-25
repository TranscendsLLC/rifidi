package com.espertech.esper.event.map;

import java.util.Map;

/**
 * For events that are maps of properties.
 */
public interface MappedEventBean
{
    /**
     * Returns property map.
     * @return properties
     */
    public Map<String, Object> getProperties();
}
