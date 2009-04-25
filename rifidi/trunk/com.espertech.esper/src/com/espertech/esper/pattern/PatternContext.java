/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.core.StatementExtensionSvcContext;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.filter.FilterService;
import com.espertech.esper.schedule.ScheduleBucket;
import com.espertech.esper.schedule.SchedulingService;
import com.espertech.esper.epl.variable.VariableService;

/**
 * Contains handles to implementations of services needed by evaluation nodes.
 */
public final class PatternContext
{
    private final int streamNumber;
    private final StatementContext statementContext;
    private final PatternStateFactory patternStateFactory;

    /**
     * Constructor.
     * @param patternStateFactory is the state node factory for the pattern
     * @param statementContext is the statement context
     * @param streamNumber is the stream number
     */
    public PatternContext(StatementContext statementContext,
                          int streamNumber,
                          PatternStateFactory patternStateFactory)
    {
        this.streamNumber = streamNumber;
        this.statementContext = statementContext;
        this.patternStateFactory = patternStateFactory;
    }

    /**
     * Returns service to use for filter evaluation.
     * @return filter evaluation service implemetation
     */
    public final FilterService getFilterService()
    {
        return statementContext.getFilterService();
    }

    /**
     * Returns service to use for schedule evaluation.
     * @return schedule evaluation service implemetation
     */
    public final SchedulingService getSchedulingService()
    {
        return statementContext.getSchedulingService();
    }

    /**
     * Returns the schedule bucket for ordering schedule callbacks within this pattern.
     * @return schedule bucket
     */
    public ScheduleBucket getScheduleBucket()
    {
        return statementContext.getScheduleBucket();
    }

    /**
     * Returns teh service providing event adaptering or wrapping.
     * @return event adapter service
     */
    public EventAdapterService getEventAdapterService()
    {
        return statementContext.getEventAdapterService();
    }

    /**
     * Returns the statement's resource handle for locking.
     * @return handle of statement
     */
    public EPStatementHandle getEpStatementHandle()
    {
        return statementContext.getEpStatementHandle();
    }

    /**
     * Returns the pattern state node factory to use.
     * @return factory for pattern state
     */
    public PatternStateFactory getPatternStateFactory()
    {
        return patternStateFactory;
    }

    /**
     * Returns the statement id.
     * @return statement id
     */
    public String getStatementId()
    {
        return statementContext.getStatementId();
    }

    /**
     * Returns the statement name.
     * @return statement name
     */
    public String getStatementName()
    {
        return statementContext.getStatementName();
    }

    /**
     * Returns the stream number.
     * @return stream number
     */
    public int getStreamNumber()
    {
        return streamNumber;
    }

    /**
     * Returns the engine URI.
     * @return engine URI
     */
    public String getEngineURI()
    {
        return statementContext.getEngineURI();
    }

    /**
     * Returns the engine instance id.
     * @return engine instance id
     */
    public String getEngineInstanceId()
    {
        return statementContext.getEngineInstanceId();
    }

    /**
     * Returns extension services context for statement (statement-specific).
     * @return extension services
     */
    public StatementExtensionSvcContext getExtensionServicesContext()
    {
        return statementContext.getExtensionServicesContext();
    }

    /**
     * Returns the variable service.
     * @return variable service
     */
    public VariableService getVariableService()
    {
        return statementContext.getVariableService();
    }
}
