/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Utility for CPU and wall time metrics.
 */
public class MetricUtil
{
    private static final Log log = LogFactory.getLog(MetricUtil.class);

    private static ThreadMXBean threadMXBean;
    private static boolean isCPUEnabled;

    /**
     * Initialize metrics mgmt.
     */
    public static void initialize()
    {
        threadMXBean = ManagementFactory.getThreadMXBean();
        isCPUEnabled = threadMXBean.isCurrentThreadCpuTimeSupported();

        if (!isCPUEnabled)
        {
            log.warn("CPU metrics reporting is not enabled by Java VM");
        }
    }

    /**
     * Returns CPU time for the current thread.
     * @return cpu current thread
     */
    public static long getCPUCurrentThread()
    {
        if (isCPUEnabled)
        {
            return threadMXBean.getCurrentThreadCpuTime();
        }
        return 0;
    }

    /**
     * Returns wall time using System#nanoTime.
     * @return wall time
     */
    public static long getWall()
    {
        return System.nanoTime();
    }
}
