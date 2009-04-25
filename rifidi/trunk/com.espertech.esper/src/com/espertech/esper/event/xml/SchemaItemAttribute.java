package com.espertech.esper.event.xml;

import java.io.Serializable;

/**
 * Represents an attribute in a schema.
 */
public class SchemaItemAttribute implements SchemaItem, Serializable
{
    private final String namespace;
    private final String name;
    private final short xsSimpleType;     // Types from XSSimpleType
    private final String typeName;

    /**
     * Ctor.
     * @param namespace namespace
     * @param name name
     * @param type attribute type
     * @param typeName attribute type name
     */
    public SchemaItemAttribute(String namespace, String name, short type, String typeName)
    {
        this.name = name;
        this.namespace = namespace;
        this.xsSimpleType = type;
        this.typeName = typeName;
    }

    /**
     * Returns the namespace.
     * @return namespace
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * Returns the name.
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the type.
     * @return type
     */
    public short getXsSimpleType()
    {
        return xsSimpleType;
    }

    /**
     * Returns the type name.
     * @return type name
     */
    public String getTypeName()
    {
        return typeName;
    }

    public String toString()
    {
        return "Attribute " + namespace + " " + name;
    }
}
