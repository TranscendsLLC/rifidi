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
 * A callback interface for indicating a change in variable value.
 */
public interface VariableChangeCallback
{
    /**
     * Indicate a change in variable value.
     * @param newValue new value
     * @param oldValue old value
     */
    public void update(Object newValue, Object oldValue);
}
