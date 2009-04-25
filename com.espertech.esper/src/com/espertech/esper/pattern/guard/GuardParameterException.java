/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern.guard;

/**
 * Thrown to indicate a validation error in guard parameterization.
 */
public class GuardParameterException extends Exception
{
    /**
     * Ctor.
     * @param message - validation error message
     */
    public GuardParameterException(String message)
    {
        super(message);
    }
}
