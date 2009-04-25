package com.espertech.esper.event;

import java.util.Map;

/**
 * Mapped beans implement this interface.
 */
public interface MappedEventBean
{
    /**
     * Returns properties.
     * @return props
     */
    public Map<String, Object> getProperties();
}
