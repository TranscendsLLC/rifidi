/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.ConfigurationInformation;

/**
 * Context for mapping a SODA statement to a statement specification, or multiple for subqueries,
 * and obtaining certain optimization information from a statement.
 */
public class StatementSpecMapContext
{
    private final EngineImportService engineImportService;
    private final VariableService variableService;
    private final ConfigurationInformation configuration;

    private boolean hasVariables;

    /**
     * Ctor.
     * @param engineImportService engine imports
     * @param variableService variable names
     * @param configuration the configuration
     */
    public StatementSpecMapContext(EngineImportService engineImportService, VariableService variableService, ConfigurationInformation configuration)
    {
        this.engineImportService = engineImportService;
        this.variableService = variableService;
        this.configuration = configuration;
    }

    /**
     * Returns the engine import service.
     * @return service
     */
    public EngineImportService getEngineImportService()
    {
        return engineImportService;
    }

    /**
     * Returns the variable service.
     * @return service
     */
    public VariableService getVariableService()
    {
        return variableService;
    }

    /**
     * Returns true if a statement has variables.
     * @return true for variables found
     */
    public boolean isHasVariables()
    {
        return hasVariables;
    }

    /**
     * Set to true to indicate that a statement has variables.
     * @param hasVariables true for variables, false for none
     */
    public void setHasVariables(boolean hasVariables)
    {
        this.hasVariables = hasVariables;
    }

    /**
     * Returns the configuration.
     * @return config
     */
    public ConfigurationInformation getConfiguration()
    {
        return configuration;
    }
}
