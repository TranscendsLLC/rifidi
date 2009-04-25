/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

/**
 * This exception is thrown to indicate a problem in administration and runtime. 
 */
public class EPException extends RuntimeException
{
    /**
     * Ctor.
     * @param message - error message
     */
    public EPException(final String message)
    {
        super(message);
    }

    /**
     * Ctor for an inner exception and message.
     * @param message - error message
     * @param cause - inner exception
     */
    public EPException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Ctor - just an inner exception.
     * @param cause - inner exception
     */
    public EPException(final Throwable cause)
    {
        super(cause);
    }
}
