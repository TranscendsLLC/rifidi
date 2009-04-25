/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.metric;

/**
 * Executor for metrics executions.
 */
public interface MetricsExecutor
{
    /**
     * Execute a metrics execution.
     * @param execution to execute
     * @param executionContext context in which to execute
     */
    public void execute(MetricExec execution, MetricExecutionContext executionContext);

    /**
     * Shut down executor.
     */
    public void destroy();
}
