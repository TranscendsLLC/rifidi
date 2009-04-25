package com.espertech.esper.core.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.espertech.esper.core.EPRuntimeImpl;
import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.filter.FilterHandleCallback;
import com.espertech.esper.collection.ArrayDequeJDK6Backport;

/**
 * Route execution work unit.
 */
public class RouteUnitMultiple implements RouteUnitRunnable
{
    private static final Log log = LogFactory.getLog(RouteUnitMultiple.class);

    private final EPRuntimeImpl epRuntime;
    private final EventBean event;
    private ArrayDequeJDK6Backport<FilterHandleCallback> callbackList;
    private EPStatementHandle handle;

    /**
     * Ctor.
     * @param epRuntime runtime to process
     * @param callbackList callback list
     * @param event event to pass
     * @param handle statement handle
     */
    public RouteUnitMultiple(EPRuntimeImpl epRuntime, ArrayDequeJDK6Backport<FilterHandleCallback> callbackList, EventBean event, EPStatementHandle handle)
    {
        this.epRuntime = epRuntime;
        this.callbackList = callbackList;
        this.event = event;
        this.handle = handle;
    }

    public void run()
    {
        try
        {
            epRuntime.processStatementFilterMultiple(handle, callbackList, event);

            epRuntime.dispatch();

            epRuntime.processThreadWorkQueue();
        }
        catch (RuntimeException e)
        {
            log.error("Unexpected error processing multiple route execution: " + e.getMessage(), e);
        }
    }

}
