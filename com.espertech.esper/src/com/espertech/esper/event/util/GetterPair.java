package com.espertech.esper.event.util;

import com.espertech.esper.client.EventPropertyGetter;

/**
 * Value-object for rendering support of a simple property value (non-nested).
 */
public class GetterPair
{
    private String name;
    private EventPropertyGetter getter;
    private OutputValueRenderer output;

    /**
     * Ctor.
     * @param getter for retrieving the value
     * @param name property name
     * @param output for rendering the getter result
     */
    public GetterPair(EventPropertyGetter getter, String name, OutputValueRenderer output)
    {
        this.getter = getter;
        this.name = name;
        this.output = output;
    }

    /**
     * Returns the getter.
     * @return getter
     */
    public EventPropertyGetter getGetter()
    {
        return getter;
    }

    /**
     * Returns the property name.
     * @return property name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the renderer for the getter return value.
     * @return renderer for result value
     */
    public OutputValueRenderer getOutput()
    {
        return output;
    }
}
