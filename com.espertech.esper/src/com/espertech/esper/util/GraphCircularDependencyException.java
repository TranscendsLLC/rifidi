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
 * Exception to represent a circular dependency.
 */
public class GraphCircularDependencyException extends Exception
{
    /**
     * Ctor.
     * @param message supplies the detailed description
     */
    public GraphCircularDependencyException(final String message)
    {
        super(message);
    }

    /**
     * Ctor.
     * @param message supplies the detailed description
     * @param cause the exception cause
     */
    public GraphCircularDependencyException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
