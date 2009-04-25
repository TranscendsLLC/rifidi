/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.named.NamedWindowService;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.vaevent.ValueAddEventService;
import com.espertech.esper.pattern.PatternObjectResolutionService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.MetaDefItem;
import com.espertech.esper.core.StatementContext;

import java.util.List;
import java.util.Set;
import java.net.URI;
import java.io.Serializable;

/**
 * Specification object for historical data poll via database SQL statement.
 */
public class DBStatementStreamSpec extends StreamSpecBase implements StreamSpecRaw, StreamSpecCompiled, MetaDefItem, Serializable
{
    private String databaseName;
    private String sqlWithSubsParams;
    private String metadataSQL;
    private static final long serialVersionUID = -4034289101265714058L;

    /**
     * Ctor.
     * @param optionalStreamName is a stream name optionally given to stream
     * @param viewSpecs is a list of views onto the stream
     * @param databaseName is the database name to poll
     * @param sqlWithSubsParams is the SQL with placeholder parameters
     * @param metadataSQL is the sample SQL to retrieve statement metadata, if any was supplied
     */
    public DBStatementStreamSpec(String optionalStreamName, List<ViewSpec> viewSpecs, String databaseName, String sqlWithSubsParams, String metadataSQL)
    {
        super(optionalStreamName, viewSpecs, new StreamSpecOptions());

        this.databaseName = databaseName;
        this.sqlWithSubsParams = sqlWithSubsParams;
        this.metadataSQL = metadataSQL;
    }

    /**
     * Returns the database name.
     * @return name of database.
     */
    public String getDatabaseName()
    {
        return databaseName;
    }

    /**
     * Returns the SQL with substitution parameters.
     * @return SQL with parameters embedded as ${stream.param}
     */
    public String getSqlWithSubsParams()
    {
        return sqlWithSubsParams;
    }

    /**
     * Returns the optional sample metadata SQL
     * @return null if not supplied, or SQL to fire to retrieve metadata
     */
    public String getMetadataSQL()
    {
        return metadataSQL;
    }

    public StreamSpecCompiled compile(StatementContext statementContext,
                                      Set<String> eventTypeReferences)
    {
        return this;
    }

}
