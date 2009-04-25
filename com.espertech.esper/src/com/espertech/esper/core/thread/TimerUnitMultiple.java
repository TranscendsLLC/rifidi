package com.espertech.esper.core.thread;

import com.espertech.esper.core.EPRuntimeImpl;
import com.espertech.esper.core.EPServicesContext;
import com.espertech.esper.core.EPStatementHandle;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Timer unit for multiple callbacks for a statement.
 */
public class TimerUnitMultiple implements TimerUnit
{
    private static final Log log = LogFactory.getLog(TimerUnitMultiple.class);

    private final EPServicesContext services;
    private final EPRuntimeImpl runtime;
    private final Object callbackObject;
    private final EPStatementHandle handle;

    /**
     * Ctor.
     * @param services engine services
     * @param runtime runtime to process
     * @param handle statement handle
     * @param callbackObject callback list
     */
    public TimerUnitMultiple(EPServicesContext services, EPRuntimeImpl runtime, EPStatementHandle handle, Object callbackObject)
    {
        this.services = services;
        this.handle = handle;
        this.runtime = runtime;
        this.callbackObject = callbackObject;
    }

    public void run()
    {
        try
        {
            EPRuntimeImpl.processStatementScheduleMultiple(handle, callbackObject, services);

            // Let listeners know of results
            runtime.dispatch();

            // Work off the event queue if any events accumulated in there via a route()
            runtime.processThreadWorkQueue();
        }
        catch (RuntimeException e)
        {
            log.error("Unexpected error processing multiple timer execution: " + e.getMessage(), e);
        }
    }
}
