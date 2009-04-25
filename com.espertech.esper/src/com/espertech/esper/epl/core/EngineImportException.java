/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

/**
 * Indicates a problem importing classes, aggregation functions and the like.
 */
public class EngineImportException extends Exception
{
    /**
     * Ctor.
     * @param msg - exception message
     */
    public EngineImportException(String msg)
    {
        super(msg);
    }

    /**
     * Ctor.
     * @param msg - exception message
     * @param ex - inner exception
     */
    public EngineImportException(String msg, Exception ex)
    {
        super(msg, ex);
    }
}
