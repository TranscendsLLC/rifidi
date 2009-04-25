/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A select-clause consists of a list of selection elements (expressions, wildcard(s), stream wildcard and the like)
 * and an optional stream selector.
 */
public class SelectClause implements Serializable
{
    private static final long serialVersionUID = 0L;

    private StreamSelector streamSelector;
    private List<SelectClauseElement> selectList;

    /**
     * Creates a wildcard select-clause, additional expressions can still be added.
     * @return select-clause
     */
    public static SelectClause createWildcard()
    {
        List<SelectClauseElement> selectList = new ArrayList<SelectClauseElement>();
        selectList.add(new SelectClauseWildcard());
        return new SelectClause(StreamSelector.ISTREAM_ONLY, selectList);
    }

    /**
     * Creates an empty select-clause to be added to via add methods.
     * @return select-clause
     */
    public static SelectClause create()
    {
        return new SelectClause(StreamSelector.ISTREAM_ONLY, new ArrayList<SelectClauseElement>());
    }

    /**
     * Creates a select-clause consisting of a list of property names.
     * @param propertyNames is the names of properties to select
     * @return select-clause
     */
    public static SelectClause create(String ...propertyNames)
    {
        List<SelectClauseElement> selectList = new ArrayList<SelectClauseElement>();
        for (String name : propertyNames)
        {
            selectList.add(new SelectClauseExpression(new PropertyValueExpression(name)));
        }
        return new SelectClause(StreamSelector.ISTREAM_ONLY, selectList);
    }

    /**
     * Creates a select-clause with a single stream wildcard selector (e.g. select streamName.* from MyStream as streamName)
     * @param streamName is the name given to a stream
     * @return select-clause
     */
    public static SelectClause createStreamWildcard(String streamName)
    {
        List<SelectClauseElement> selectList = new ArrayList<SelectClauseElement>();
        selectList.add(new SelectClauseStreamWildcard(streamName, null));
        return new SelectClause(StreamSelector.ISTREAM_ONLY, selectList);
    }

    /**
     * Creates a wildcard select-clause, additional expressions can still be added.
     * @param streamSelector can be used to select insert or remove streams
     * @return select-clause
     */
    public static SelectClause createWildcard(StreamSelector streamSelector)
    {
        List<SelectClauseElement> selectList = new ArrayList<SelectClauseElement>();
        selectList.add(new SelectClauseWildcard());
        return new SelectClause(streamSelector, selectList);
    }

    /**
     * Creates an empty select-clause.
     * @param streamSelector can be used to select insert or remove streams
     * @return select-clause
     */
    public static SelectClause create(StreamSelector streamSelector)
    {
        return new SelectClause(streamSelector, new ArrayList<SelectClauseElement>());
    }

    /**
     * Creates a select-clause consisting of a list of property names.
     * @param propertyNames is the names of properties to select
     * @param streamSelector can be used to select insert or remove streams
     * @return select-clause
     */
    public static SelectClause create(StreamSelector streamSelector, String ...propertyNames)
    {
        List<SelectClauseElement> selectList = new ArrayList<SelectClauseElement>();
        for (String name : propertyNames)
        {
            selectList.add(new SelectClauseExpression(new PropertyValueExpression(name)));
        }
        return new SelectClause(streamSelector, selectList);
    }

    /**
     * Ctor.
     * @param streamSelector selects the stream
     * @param selectList is a list of elements in the select-clause
     */
    protected SelectClause(StreamSelector streamSelector, List<SelectClauseElement> selectList)
    {
        this.streamSelector = streamSelector;
        this.selectList = selectList;
    }

    /**
     * Adds property names to be selected.
     * @param propertyNames is a list of property names to add
     * @return clause
     */
    public SelectClause add(String ...propertyNames)
    {
        for (String name : propertyNames)
        {
            selectList.add(new SelectClauseExpression(new PropertyValueExpression(name)));
        }
        return this;
    }

    /**
     * Adds a single property name and an "as"-asName for the column.
     * @param propertyName name of property
     * @param asName is the "as"-asName for the column
     * @return clause
     */
    public SelectClause addWithAsProvidedName(String propertyName, String asName)
    {
        selectList.add(new SelectClauseExpression(new PropertyValueExpression(propertyName), asName));
        return this;
    }

    /**
     * Adds an expression to the select clause.
     * @param expression to add
     * @return clause
     */
    public SelectClause add(Expression expression)
    {
        selectList.add(new SelectClauseExpression(expression));
        return this;
    }

    /**
     * Adds an expression to the select clause and an "as"-asName for the column.
     * @param expression to add
     * @param asName is the "as"-provided for the column
     * @return clause
     */
    public SelectClause add(Expression expression, String asName)
    {
        selectList.add(new SelectClauseExpression(expression, asName));
        return this;
    }

    /**
     * Returns the stream selector.
     * @return stream selector
     */
    public StreamSelector getStreamSelector()
    {
        return streamSelector;
    }

    /**
     * Returns the list of expressions in the select clause.
     * @return list of expressions with column names
     */
    public List<SelectClauseElement> getSelectList()
    {
        return selectList;
    }

    /**
     * Adds to the select-clause a stream wildcard selector (e.g. select streamName.* from MyStream as streamName)
     * @param streamName is the name given to a stream
     * @return select-clause
     */
    public SelectClause addStreamWildcard(String streamName)
    {
        selectList.add(new SelectClauseStreamWildcard(streamName, null));
        return this;
    }

    /**
     * Adds to the select-clause a  wildcard selector (e.g. select * from MyStream as streamName)
     * @return select-clause
     */
    public SelectClause addWildcard()
    {
        selectList.add(new SelectClauseWildcard());
        return this;
    }

    /**
     * Adds to the select-clause a stream wildcard selector with column name (e.g. select streamName.* as colName from MyStream as streamName)
     * @param streamName is the name given to a stream
     * @param columnName the name given to the column
     * @return select-clause
     */
    public SelectClause addStreamWildcard(String streamName, String columnName)
    {
        selectList.add(new SelectClauseStreamWildcard(streamName, columnName));
        return this;
    }

    /**
     * Sets the stream selector.
     * @param streamSelector stream selector to set
     * @return select clause
     */
    public SelectClause setStreamSelector(StreamSelector streamSelector)
    {
        this.streamSelector = streamSelector;
        return this;
    }

    /**
     * Sets the list of expressions in the select clause.
     * @param selectList list of expressions with column names
     */
    public void setSelectList(List<SelectClauseElement> selectList)
    {
        this.selectList = selectList;
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPL(StringWriter writer)
    {
        writer.write("select ");

        if (streamSelector == StreamSelector.ISTREAM_ONLY)
        {
            // the default, no action
        }
        else if (streamSelector == StreamSelector.RSTREAM_ONLY)
        {
            writer.write("rstream ");
        }
        else if (streamSelector == StreamSelector.RSTREAM_ISTREAM_BOTH)
        {
            writer.write("irstream ");
        }

        String delimiter = "";
        for (SelectClauseElement element : selectList)
        {
            writer.write(delimiter);
            element.toEPLElement(writer);
            delimiter = ", ";
        }
        writer.write(' ');
    }
}
