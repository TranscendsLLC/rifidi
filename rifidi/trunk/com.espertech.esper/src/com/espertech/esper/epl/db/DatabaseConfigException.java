/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.db;

/**
 * Exception to indicate that a stream name could not be resolved.
 */
public class DatabaseConfigException extends Exception
{
    /**
     * Ctor.
     * @param msg - message
     */
    public DatabaseConfigException(String msg)
    {
        super(msg);
    }

    /**
     * Ctor.
     * @param message - error message
     * @param cause - cause is the inner exception
     */
    public DatabaseConfigException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
