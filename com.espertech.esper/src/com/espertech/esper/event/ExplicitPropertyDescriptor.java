package com.espertech.esper.event;

import com.espertech.esper.client.EventPropertyDescriptor;
import com.espertech.esper.client.EventPropertyGetter;

/**
 * Descriptor for explicit properties for use with {@link BaseConfigurableEventType}.
 */
public class ExplicitPropertyDescriptor
{
    private final EventPropertyGetter getter;
    private final EventPropertyDescriptor descriptor;
    private final String optionalFragmentTypeName;
    private final boolean isFragmentArray;

    /**
     * Ctor.
     * @param descriptor property descriptor
     * @param getter getter for values
     * @param fragmentArray true if array fragment
     * @param optionalFragmentTypeName null if not a fragment, else fragment type name
     */
    public ExplicitPropertyDescriptor(EventPropertyDescriptor descriptor, EventPropertyGetter getter, boolean fragmentArray, String optionalFragmentTypeName)
    {
        this.descriptor = descriptor;
        this.getter = getter;
        isFragmentArray = fragmentArray;
        this.optionalFragmentTypeName = optionalFragmentTypeName;
    }

    /**
     * Returns the property descriptor.
     * @return property descriptor
     */
    public EventPropertyDescriptor getDescriptor()
    {
        return descriptor;
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
     * Returns the fragment event type name, or null if none defined.
     * @return fragment type name
     */
    public String getOptionalFragmentTypeName()
    {
        return optionalFragmentTypeName;
    }

    /**
     * Returns true if an indexed, or false if not indexed.
     * @return fragment indicator
     */
    public boolean isFragmentArray()
    {
        return isFragmentArray;
    }

    public String toString()
    {
        return descriptor.getPropertyName();
    }
}
