/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * Helper class to find and invoke a class constructors that matches the types of arguments supplied.
 */
public class ConstructorHelper
{
    private static Class[] EMPTY_OBJECT_ARRAY_TYPE = new Class[] { (new Object[0]).getClass() };

    /**
     * Find and invoke constructor matching the argument number and types returning an instance
     * of given class.
     * @param clazz is the class of instance to construct
     * @param arguments is the arguments for the constructor to match in number and type
     * @return instance of class
     * @throws IllegalAccessException thrown if no access to class
     * @throws NoSuchMethodException thrown when the constructor is not found
     * @throws InvocationTargetException thrown when the ctor throws and exception
     * @throws InstantiationException thrown when the class cannot be loaded
     */
    public static Object invokeConstructor(Class clazz, Object[] arguments) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException
    {
        if (log.isDebugEnabled())
        {
            logConstructors(clazz);
        }

        Class parameterTypes[] = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++)
        {
            parameterTypes[i] = arguments[i].getClass();
        }

        // Find a constructor that matches exactly
        Constructor ctor = getRegularConstructor(clazz, parameterTypes);
        if (ctor != null)
        {
            return ctor.newInstance(arguments);
        }

        // Find a constructor with the same number of assignable parameters (such as int -> Integer).
        ctor = findMatchingConstructor(clazz, parameterTypes);
        if (ctor != null)
        {
            return ctor.newInstance(arguments);
        }

        // Find an Object[] constructor, which always matches (throws an exception if not found)
        ctor = getObjectArrayConstructor(clazz);
        return ctor.newInstance(new Object[] { arguments });
    }

    private static Constructor findMatchingConstructor(Class clazz, Class parameterTypes[])
    {
        Constructor[] ctors = clazz.getConstructors();

        for (int i = 0; i < ctors.length; i++)
        {
            Class[] ctorParams = ctors[i].getParameterTypes();

            if (isAssignmentCompatible(parameterTypes, ctorParams))
            {
                return ctors[i];
            }
        }

        return null;
    }

    private static boolean isAssignmentCompatible(Class[] parameterTypes, Class[] ctorParams)
    {
        if (parameterTypes.length != ctorParams.length)
        {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++)
        {
            if (!JavaClassHelper.isAssignmentCompatible(ctorParams[i], parameterTypes[i]))
            {
                return false;
            }
        }

        return true;
    }

    private static Constructor getRegularConstructor(Class clazz, Class parameterTypes[])
    {

        // Try to find the matching constructor
        try
        {
            Constructor ctor = clazz.getConstructor(parameterTypes);

            try
            {
                ctor.setAccessible(true);
            }
            catch (SecurityException se)
            {
                // No action
            }

            return ctor;
        }
        catch (NoSuchMethodException e)
        {
            // Thats ok
        }
        return null;
    }

    // Try to find an Object[] constructor
    private static Constructor getObjectArrayConstructor(Class clazz) throws NoSuchMethodException
    {
        return clazz.getConstructor(EMPTY_OBJECT_ARRAY_TYPE);
    }

    private static void logConstructors(Class clazz)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".invokeConstructor Constructors for class " + clazz);
        }

        Constructor[] ctors = clazz.getConstructors();
        for (int i = 0, size = ctors.length; i < size; i++)
        {
            Class[] ctorParams = ctors[i].getParameterTypes();
            if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
            {
                log.debug(".invokeConstructor Constructor " + i + ' ' + Arrays.toString(ctorParams));
            }
        }
    }

    private static final Log log = LogFactory.getLog(ConstructorHelper.class);
}
