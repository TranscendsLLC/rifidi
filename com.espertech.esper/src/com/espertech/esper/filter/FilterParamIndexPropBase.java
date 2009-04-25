/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.util.JavaClassHelper;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * Each implementation of this abstract class represents an index of filter parameter constants supplied in filter
 * parameters in filter specifications that feature the same event property and operator.
 * <p>
 * For example, a filter with a parameter of "count EQUALS 10" would be represented as index
 * for a property named "count" and for a filter operator typed "EQUALS". The index
 * would store a value of "10" in its internal structure.
 * <p>
 * Implementations make sure that the type of the Object constant in get and put calls matches the event property type.
 */
public abstract class FilterParamIndexPropBase extends FilterParamIndexBase
{
    private final String propertyName;
    private final EventPropertyGetter getter;
    private final Class propertyBoxedType;

    /**
     * Constructor.
     * @param propertyName is the name of the event property the index goes against
     * @param filterOperator is the type of comparison performed.
     * @param eventType is the event type the index will handle.
     */
    public FilterParamIndexPropBase(String propertyName, FilterOperator filterOperator, EventType eventType)
    {
        super(filterOperator);
        this.propertyName = propertyName;
        this.getter = eventType.getGetter(propertyName);
        this.propertyBoxedType = JavaClassHelper.getBoxedType(eventType.getPropertyType(propertyName));
        if (getter == null)
        {
            throw new IllegalArgumentException("Property named '" + propertyName + "' not valid for event type ");
        }
    }

    /**
     * Get the event evaluation instance associated with the constant. Returns null if no entry found for the constant.
     * The calling class must make sure that access to the underlying resource is protected
     * for multi-threaded access, the getReadWriteLock() method must supply a lock for this purpose.
     * @param filterConstant is the constant supplied in the event filter parameter
     * @return event evaluator stored for the filter constant, or null if not found
     */
    protected abstract EventEvaluator get(Object filterConstant);

    /**
     * Store the event evaluation instance for the given constant. Can override an existing value
     * for the same constant.
     * The calling class must make sure that access to the underlying resource is protected
     * for multi-threaded access, the getReadWriteLock() method must supply a lock for this purpose.
     * @param filterConstant is the constant supplied in the filter parameter
     * @param evaluator to be stored for the constant
     */
    protected abstract void put(Object filterConstant, EventEvaluator evaluator);

    /**
     * Remove the event evaluation instance for the given constant. Returns true if
     * the constant was found, or false if not.
     * The calling class must make sure that access to the underlying resource is protected
     * for multi-threaded writes, the getReadWriteLock() method must supply a lock for this purpose.
     * @param filterConstant is the value supplied in the filter paremeter
     * @return true if found and removed, false if not found
     */
    protected abstract boolean remove(Object filterConstant);

    /**
     * Return the number of distinct filter parameter constants stored.
     * The calling class must make sure that access to the underlying resource is protected
     * for multi-threaded writes, the getReadWriteLock() method must supply a lock for this purpose.
     * @return Number of entries in index
     */
    protected abstract int size();

    /**
     * Supplies the lock for protected access.
     * @return lock
     */
    protected abstract ReadWriteLock getReadWriteLock();

    /**
     * Returns the name of the property to get the value for to match against the values
     * contained in the index.
     * @return event property name
     */
    public final String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Returns getter for property.
     * @return property value getter
     */
    public EventPropertyGetter getGetter()
    {
        return getter;
    }

    /**
     * Returns boxed property type.
     * @return boxed property type
     */
    public Class getPropertyBoxedType()
    {
        return propertyBoxedType;
    }

    public final String toString()
    {
        return super.toString() +
               " propName=" + propertyName +
               " propertyBoxedType=" + propertyBoxedType.getName();
    }
}
