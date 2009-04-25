/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join;

import com.espertech.esper.epl.core.ResultSetProcessor;

/**
 * Method for preloading events for a given stream onto the stream's indexes, from a buffer already associated with a stream.
 */
public interface JoinPreloadMethod
{
    /**
     * Initialize a stream from the stream buffers data.
     * @param stream to initialize and load indexes
     */
    public void preloadFromBuffer(int stream);

    /**
     * Initialize the result set process for the purpose of grouping and aggregation
     * from the join result set.
     * @param resultSetProcessor is the grouping and aggregation result processing
     */
    public void preloadAggregation(ResultSetProcessor resultSetProcessor);
}
