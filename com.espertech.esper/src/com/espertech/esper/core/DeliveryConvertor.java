/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

/**
 * Converts a row of column selection results into a result for dispatch to a method.
 */
public interface DeliveryConvertor
{
    /**
     * Convert result row to dispatchable.
     * @param row to convert
     * @return converted row
     */
    public Object[] convertRow(Object[] row);
}
