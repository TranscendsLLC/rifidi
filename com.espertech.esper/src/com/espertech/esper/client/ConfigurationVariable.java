/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

import java.io.Serializable;

/**
 * Provides variable configuration.
 */
public class ConfigurationVariable implements Serializable
{
    private Class type;
    private Object initializationValue;
    private static final long serialVersionUID = 4273849084807284503L;

    /**
     * Returns the variable type.
     * <p>
     * Variables are scalar values and primitive or boxed Java builtin types are accepted.
     * @return variable type
     */
    public Class getType()
    {
        return type;
    }

    /**
     * Sets the variable type.
     * <p>
     * Variables are scalar values and primitive or boxed Java builtin types are accepted.
     * @param type variable type
     */
    public void setType(Class type)
    {
        this.type = type;
    }

    /**
     * Returns the initialization value, or null if none was supplied.
     * <p>
     * String-type initialization values for numeric or boolean types are allowed and are parsed.
     * @return default value
     */
    public Object getInitializationValue()
    {
        return initializationValue;
    }

    /**
     * Sets the variable type.
     * <p>
     * Variables are scalar values and primitive or boxed Java builtin types are accepted.
     * @param initializationValue the default value or null if the default value is null
     */
    public void setInitializationValue(Object initializationValue)
    {
        this.initializationValue = initializationValue;
    }
}
