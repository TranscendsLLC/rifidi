/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.epl.spec.OnTriggerDesc;
import com.espertech.esper.epl.spec.CreateWindowDesc;

import java.util.Map;
import java.net.URI;

/**
 * Interface for a factory class that makes statement context specific to a statement.
 */
public interface StatementContextFactory
{
    /**
     * Create a new statement context consisting of statement-level services.
     * @param statementId is the statement is
     * @param statementName is the statement name
     * @param expression is the statement expression
     * @param engineServices is engine services
     * @param optAdditionalContext addtional context to pass to the statement
     * @param optOnTriggerDesc the on-delete statement descriptor for named window context creation
     * @param optCreateWindowDesc the create-window statement descriptor for named window context creation
     * @param hasVariables indicator whether the statement uses variables anywhere in the statement
     * @param isFireAndForget if the statement context is for a fire-and-forget statement
     * @return statement context
     */
    public StatementContext makeContext(String statementId,
                                        String statementName,
                                        String expression,
                                        boolean hasVariables,
                                        EPServicesContext engineServices,
                                        Map<String, Object> optAdditionalContext,
                                        OnTriggerDesc optOnTriggerDesc,
                                        CreateWindowDesc optCreateWindowDesc,
                                        boolean isFireAndForget);
}
