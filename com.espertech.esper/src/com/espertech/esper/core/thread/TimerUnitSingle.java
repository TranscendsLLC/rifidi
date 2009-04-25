package com.espertech.esper.core.thread;

import com.espertech.esper.core.EPRuntimeImpl;
import com.espertech.esper.core.EPStatementHandleCallback;
import com.espertech.esper.core.EPServicesContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Timer unit for a single callback for a statement.
 */
public class TimerUnitSingle implements TimerUnit
{
    private static final Log log = LogFactory.getLog(TimerUnitSingle.class);

    private final EPServicesContext services;
    private final EPRuntimeImpl runtime;
    private final EPStatementHandleCallback handleCallback;

    /**
     * Ctor.
     * @param services engine services
     * @param runtime runtime to process
     * @param handleCallback callback 
     */
    public TimerUnitSingle(EPServicesContext services, EPRuntimeImpl runtime, EPStatementHandleCallback handleCallback)
    {
        this.services = services;
        this.runtime = runtime;
        this.handleCallback = handleCallback;
    }

    public void run()
    {
        try
        {
            EPRuntimeImpl.processStatementScheduleSingle(handleCallback, services);

            runtime.dispatch();

            runtime.processThreadWorkQueue();
        }
        catch (RuntimeException e)
        {
            log.error("Unexpected error processing timer execution: " + e.getMessage(), e);
        }
    }
}
