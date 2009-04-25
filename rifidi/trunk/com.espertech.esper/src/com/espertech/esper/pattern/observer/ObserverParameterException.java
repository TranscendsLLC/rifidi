/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern.observer;

/**
 * Thrown to indicate a validation error in guard parameterization.
 */
public class ObserverParameterException extends Exception
{
    /**
     * Ctor.
     * @param message - validation error message
     */
    public ObserverParameterException(String message)
    {
        super(message);
    }

    /**
     * Ctor.
     * @param message the error message
     * @param cause the causal exception
     */
    public ObserverParameterException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
