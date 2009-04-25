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
import java.util.Arrays;

/**
 * Create a named window, defining the parameter of the named window such as window name and data window view name(s).
 */
public class CreateWindowClause implements Serializable
{
    private static final long serialVersionUID = 0L;

    private String windowName;
    private List<View> views;
    private boolean isInsert;
    private Expression insertWhereClause;

    /**
     * Creates a clause to create a named window.
     * @param windowName is the name of the named window
     * @param view is a data window view
     * @return create window clause
     */
    public static CreateWindowClause create(String windowName, View view)
    {
        return new CreateWindowClause(windowName, new View[] {view});
    }

    /**
     * Creates a clause to create a named window.
     * @param windowName is the name of the named window
     * @param views is the data window views
     * @return create window clause
     */
    public static CreateWindowClause create(String windowName, View... views)
    {
        return new CreateWindowClause(windowName, views);
    }

    /**
     * Adds an un-parameterized view to the named window.
     * @param namespace is the view namespace, for example "win" for most data windows
     * @param name is the view name, for example "length" for a length window
     * @return named window creation clause
     */
    public CreateWindowClause addView(String namespace, String name)
    {
        views.add(View.create(namespace, name));
        return this;
    }

    /**
     * Adds a parameterized view to the named window.
     * @param namespace is the view namespace, for example "win" for most data windows
     * @param name is the view name, for example "length" for a length window
     * @param parameters is a list of view parameters
     * @return named window creation clause
     */
    public CreateWindowClause addView(String namespace, String name, List<Expression> parameters)
    {
        views.add(View.create(namespace, name, parameters));
        return this;
    }

    /**
     * Adds a parameterized view to the named window.
     * @param namespace is the view namespace, for example "win" for most data windows
     * @param name is the view name, for example "length" for a length window
     * @param parameters is a list of view parameters
     * @return named window creation clause
     */
    public CreateWindowClause addView(String namespace, String name, Expression... parameters)
    {
        views.add(View.create(namespace, name, parameters));
        return this;
    }

    /**
     * Ctor.
     * @param windowName is the name of the window to create
     * @param viewArr is the list of data window views
     */
    public CreateWindowClause(String windowName, View[] viewArr)
    {
        this.windowName = windowName;
        views = new ArrayList<View>();
        if (viewArr != null)
        {
            views.addAll(Arrays.asList(viewArr));
        }
    }

    /**
     * Ctor.
     * @param windowName is the name of the window to create
     * @param views is a list of data window views
     */
    public CreateWindowClause(String windowName, List<View> views)
    {
        this.windowName = windowName;
        this.views = views;
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPL(StringWriter writer)
    {
        writer.write("create window ");
        writer.write(windowName);
        ProjectedStream.toEPLViews(writer, views);
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPLInsertPart(StringWriter writer)
    {
        if (isInsert)
        {
            writer.write(" insert");
            if (insertWhereClause != null)
            {
                writer.write(" where ");
                insertWhereClause.toEPL(writer);
            }
        }
    }

    /**
     * Returns the window name.
     * @return window name
     */
    public String getWindowName()
    {
        return windowName;
    }

    /**
     * Sets the window name.
     * @param windowName is the name to set
     */
    public void setWindowName(String windowName)
    {
        this.windowName = windowName;
    }

    /**
     * Returns the views onto the named window.
     * @return named window data views
     */
    public List<View> getViews()
    {
        return views;
    }

    /**
     * Returns true if inserting from another named window, false if not.
     * @return insert from named window
     */
    public boolean isInsert()
    {
        return isInsert;
    }

    /**
     * Filter expression for inserting from another named window, or null if not inserting from another named window.
     * @return filter expression
     */
    public Expression getInsertWhereClause()
    {
        return insertWhereClause;
    }

    /**
     * Sets flag indicating that an insert from another named window should take place at the time of window creation.
     * @param insert true for insert from another named window
     * @return clause
     */
    public CreateWindowClause setInsert(boolean insert)
    {
        isInsert = insert;
        return this;
    }

    /**
     * Sets the filter expression for inserting from another named window
     * @param insertWhereClause filter expression
     * @return create window clause
     */
    public CreateWindowClause setInsertWhereClause(Expression insertWhereClause)
    {
        this.insertWhereClause = insertWhereClause;
        return this;
    }

    /**
     * Sets the views onto the named window.
     * @param views to set
     */
    public void setViews(List<View> views)
    {
        this.views = views;
    }
}
