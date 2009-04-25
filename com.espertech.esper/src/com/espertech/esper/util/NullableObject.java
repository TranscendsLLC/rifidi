/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.util;

/**
 * A generic class to hold an object that may itself be a null value versus an undefined (not existing) value.
 * <p>
 * The presence of a reference indicates that a value exists, the absence of a reference to this object indicates
 * that there is no value (similar to a Pair<Object, Boolean>).
 */
public class NullableObject<T>
{
    private T object;

    /**
     * Ctor.
     * @param object the object to contain
     */
    public NullableObject(T object)
    {
        this.object = object;
    }

    /**
     * Returns the contained value.
     * @return contained value
     */
    public T getObject()
    {
        return object;
    }

    /**
     * Sets a new contained value.
     * @param object value to set
     */
    public void setObject(T object)
    {
        this.object = object;
    }
}
