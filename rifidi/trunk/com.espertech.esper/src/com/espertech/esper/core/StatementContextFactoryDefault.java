/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.EPStatementException;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.MethodResolutionServiceImpl;
import com.espertech.esper.epl.join.JoinSetComposerFactoryImpl;
import com.espertech.esper.epl.metric.StatementMetricHandle;
import com.espertech.esper.epl.spec.CreateWindowDesc;
import com.espertech.esper.epl.spec.OnTriggerDesc;
import com.espertech.esper.epl.spec.OnTriggerWindowDesc;
import com.espertech.esper.epl.spec.PluggableObjectCollection;
import com.espertech.esper.pattern.*;
import com.espertech.esper.schedule.ScheduleBucket;
import com.espertech.esper.util.ManagedLock;
import com.espertech.esper.view.StatementStopServiceImpl;
import com.espertech.esper.view.ViewEnumHelper;
import com.espertech.esper.view.ViewResolutionService;
import com.espertech.esper.view.ViewResolutionServiceImpl;

import java.util.Map;

/**
 * Default implementation for making a statement-specific context class.
 */
public class StatementContextFactoryDefault implements StatementContextFactory
{
    private PluggableObjectCollection viewClasses;
    private PluggableObjectCollection patternObjectClasses;

    /**
     * Ctor.
     * @param viewPlugIns is the view plug-in object descriptions
     * @param plugInPatternObj is the pattern plug-in object descriptions
     */
    public StatementContextFactoryDefault(PluggableObjectCollection viewPlugIns, PluggableObjectCollection plugInPatternObj)
    {
        viewClasses = new PluggableObjectCollection();
        viewClasses.addObjects(viewPlugIns);
        viewClasses.addObjects(ViewEnumHelper.getBuiltinViews());

        patternObjectClasses = new PluggableObjectCollection();
        patternObjectClasses.addObjects(plugInPatternObj);
        patternObjectClasses.addObjects(PatternObjectHelper.getBuiltinPatternObjects());
    }

    public StatementContext makeContext(String statementId,
                                    String statementName,
                                    String expression,
                                    boolean hasVariables,
                                    EPServicesContext engineServices,
                                    Map<String, Object> optAdditionalContext,
                                    OnTriggerDesc optOnTriggerDesc,
                                    CreateWindowDesc optCreateWindowDesc,
                                    boolean isFireAndForget)
    {
        // Allocate the statement's schedule bucket which stays constant over it's lifetime.
        // The bucket allows callbacks for the same time to be ordered (within and across statements) and thus deterministic.
        ScheduleBucket scheduleBucket = engineServices.getSchedulingService().allocateBucket();

        // Create a lock for the statement
        ManagedLock statementResourceLock = null;

        // For on-delete statements, use the create-named-window statement lock
        if ((optOnTriggerDesc != null) && (optOnTriggerDesc instanceof OnTriggerWindowDesc))
        {
            String windowName = ((OnTriggerWindowDesc) optOnTriggerDesc).getWindowName();
            statementResourceLock = engineServices.getNamedWindowService().getNamedWindowLock(windowName);
            if (statementResourceLock == null)
            {
                throw new EPStatementException("Named window '" + windowName + "' has not been declared", expression);
            }
        }
        // For creating a named window, save the lock for use with on-delete statements
        else if (optCreateWindowDesc != null)
        {
            statementResourceLock = engineServices.getNamedWindowService().getNamedWindowLock(optCreateWindowDesc.getWindowName());
            if (statementResourceLock == null)
            {
                statementResourceLock = engineServices.getStatementLockFactory().getStatementLock(statementName, expression);
                engineServices.getNamedWindowService().addNamedWindowLock(optCreateWindowDesc.getWindowName(), statementResourceLock);
            }
        }
        else
        {
            statementResourceLock = engineServices.getStatementLockFactory().getStatementLock(statementName, expression);
        }

        StatementMetricHandle stmtMetric = null;
        if (!isFireAndForget)
        {
            stmtMetric = engineServices.getMetricsReportingService().getStatementHandle(statementId, statementName);
        }
        
        EPStatementHandle epStatementHandle = new EPStatementHandle(statementId, statementResourceLock, expression, hasVariables, stmtMetric);

        MethodResolutionService methodResolutionService = new MethodResolutionServiceImpl(engineServices.getEngineImportService(), engineServices.getConfigSnapshot().getEngineDefaults().getExpression().isUdfCache());

        PatternContextFactory patternContextFactory = new PatternContextFactoryDefault();

        ViewResolutionService viewResolutionService = new ViewResolutionServiceImpl(viewClasses);
        PatternObjectResolutionService patternResolutionService = new PatternObjectResolutionServiceImpl(patternObjectClasses);

        // Create statement context
        return new StatementContext(engineServices.getEngineURI(),
                engineServices.getEngineInstanceId(),
                statementId,
                statementName,
                expression,
                engineServices.getSchedulingService(),
                scheduleBucket,
                engineServices.getEventAdapterService(),
                epStatementHandle,
                viewResolutionService,
                patternResolutionService,
                null,   // no statement extension context
                new StatementStopServiceImpl(),
                methodResolutionService,
                patternContextFactory,
                engineServices.getFilterService(),
                new JoinSetComposerFactoryImpl(),
                engineServices.getOutputConditionFactory(),
                engineServices.getNamedWindowService(),
                engineServices.getVariableService(),
                new StatementResultServiceImpl(engineServices.getStatementLifecycleSvc(), engineServices.getMetricsReportingService(), engineServices.getThreadingService()),
                engineServices.getEngineSettingsService().getPlugInEventTypeResolutionURIs(),
                engineServices.getValueAddEventService(),
                engineServices.getConfigSnapshot());
    }
}
