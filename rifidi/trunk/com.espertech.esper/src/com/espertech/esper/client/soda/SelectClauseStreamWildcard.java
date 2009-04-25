/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * For use in a select clause, this element in a select clause defines that for a given stream we want to
 * select the underlying type. Most often used in joins to select wildcard from one of the joined streams.
 * <p>
 * For example:
 * <pre>select streamOne.* from StreamOne as streamOne, StreamTwo as streamTwo</pre>
 */
public class SelectClauseStreamWildcard implements SelectClauseElement
{
    private String streamName;
    private String optionalColumnName;

    /**
     * Ctor.
     * @param streamName is the name assigned to a stream
     * @param optionalColumnName is the name to assign to the column carrying the streams generated events, or
     * null if the event should not appear in a column
     */
    public SelectClauseStreamWildcard(String streamName, String optionalColumnName)
    {
        this.streamName = streamName;
        this.optionalColumnName = optionalColumnName;
    }

    /**
     * Returns the stream name (e.g. select streamName.* as colName from MyStream as streamName)
     * @return name
     */
    public String getStreamName()
    {
        return streamName;
    }

    /**
     * Returns the optional column name (e.g. select streamName.* as colName from MyStream as streamName)
     * @return name of column, or null if none defined
     */
    public String getOptionalColumnName()
    {
        return optionalColumnName;
    }

    /**
     * Renders the element in textual representation.
     * @param writer to output to
     */
    public void toEPLElement(StringWriter writer)
    {
        writer.write(streamName);
        writer.write(".*");
        if (optionalColumnName != null)
        {
            writer.write(" as ");
            writer.write(optionalColumnName);
        }
    }
}
