/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.client.EventBean;

/**
 * Converts from a map of prior matching events to a events per stream for resultion by expressions.
 */
public interface MatchedEventConvertor
{
    /**
     * Converts pattern matching events to events per stream.
     * @param events pattern partial matches
     * @return events per stream
     */
    public EventBean[] convert(MatchedEventMap events);
}