package com.espertech.esper.event.property;

/**
 * Descriptor for a type and its generic type, if any.
 */
public class GenericPropertyDesc
{
    private static final GenericPropertyDesc objectGeneric = new GenericPropertyDesc(Object.class);

    private final Class type;
    private final Class generic;

    /**
     * Ctor.
     * @param type the type
     * @param generic its generic type parameter, if any
     */
    public GenericPropertyDesc(Class type, Class generic)
    {
        this.type = type;
        this.generic = generic;
    }

    /**
     * Ctor.
     * @param type the type
     */
    public GenericPropertyDesc(Class type)
    {
        this.type = type;
        this.generic = null;
    }

    /**
     * Returns the type.
     * @return type
     */
    public Class getType()
    {
        return type;
    }

    /**
     * Returns the generic parameter, or null if none.
     * @return generic parameter
     */
    public Class getGeneric()
    {
        return generic;
    }

    /**
     * Object.class type.
     * @return type descriptor
     */
    public static GenericPropertyDesc getObjectGeneric()
    {
        return objectGeneric;
    }
}
