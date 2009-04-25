/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.variable;

/**
 * Exception indicating a variable type error.
 */
public class VariableTypeException extends VariableDeclarationException
{
    /**
     * Ctor.
     * @param msg the exception message.
     */
    public VariableTypeException(String msg)
    {
        super(msg);
    }
}
