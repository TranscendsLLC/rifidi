/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.util;

import com.espertech.esper.event.EventAdapterException;
import com.espertech.esper.type.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * Interface for number coercion.
 */
public interface SimpleNumberCoercer
{
    /**
     * Coerce the given number to a previously determined type, assuming the type is a Boxed type. Allows coerce to lower resultion number.
     * Does't coerce to primitive types.
     * @param numToCoerce is the number to coerce to the given type
     * @return the numToCoerce as a value in the given result type
     */
    public Number coerceBoxed(Number numToCoerce);
}
