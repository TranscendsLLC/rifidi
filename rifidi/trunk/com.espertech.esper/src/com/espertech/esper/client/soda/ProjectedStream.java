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
import java.util.List;

/**
 * Abstract base class for streams that can be projected via views providing data window, uniqueness or other projections
 * or deriving further information from streams.
 */
public abstract class ProjectedStream extends Stream
{
    private List<View> views;
    private boolean isUnidirectional;
    private boolean isRetainUnion;
    private boolean isRetainIntersection;

    /**
     * Represent as textual.
     * @param writer to output to
     */
    public abstract void toEPLProjectedStream(StringWriter writer);

    /**
     * Ctor.
     * @param views is a list of views upon the stream
     * @param optStreamName is the stream as-name, or null if unnamed 
     */
    protected ProjectedStream(List<View> views, String optStreamName)
    {
        super(optStreamName);
        this.views = views;
    }

    /**
     * Adds an un-parameterized view to the stream.
     * @param namespace is the view namespace, for example "win" for most data windows
     * @param name is the view name, for example "length" for a length window
     * @return stream
     */
    public ProjectedStream addView(String namespace, String name)
    {
        views.add(View.create(namespace, name));
        return this;
    }

    /**
     * Adds a parameterized view to the stream.
     * @param namespace is the view namespace, for example "win" for most data windows
     * @param name is the view name, for example "length" for a length window
     * @param parameters is a list of view parameters
     * @return stream
     */
    public ProjectedStream addView(String namespace, String name, List<Expression> parameters)
    {
        views.add(View.create(namespace, name, parameters));
        return this;
    }

    /**
     * Adds a parameterized view to the stream.
     * @param namespace is the view namespace, for example "win" for most data windows
     * @param name is the view name, for example "length" for a length window
     * @param parameters is a list of view parameters
     * @return stream
     */
    public ProjectedStream addView(String namespace, String name, Expression ...parameters)
    {
        views.add(View.create(namespace, name, parameters));
        return this;
    }

    /**
     * Add a view to the stream.
     * @param view to add
     * @return stream
     */
    public ProjectedStream addView(View view)
    {
        views.add(view);
        return this;
    }

    /**
     * Returns the list of views added to the stream.
     * @return list of views
     */
    public List<View> getViews()
    {
        return views;
    }

    /**
     * Sets the list of views onto the stream.
     * @param views list of views
     */
    public void setViews(List<View> views)
    {
        this.views = views;
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPLStream(StringWriter writer)
    {
        toEPLProjectedStream(writer);
        toEPLViews(writer, views);
        if (isUnidirectional)
        {
            writer.write(" unidirectional");
        }
        else if (isRetainUnion)
        {
            writer.write(" retain-union");
        }
        else if (isRetainIntersection)
        {
            writer.write(" retain-intersection");
        }
    }

    /**
     * Returns true if the stream as unidirectional, for use in unidirectional joins.
     * @return true for unidirectional stream, applicable only for joins
     */
    public boolean isUnidirectional()
    {
        return isUnidirectional;
    }

    /**
     * Set to true to indicate that a stream is unidirectional, for use in unidirectional joins.
     * @param isUnidirectional true for unidirectional stream, applicable only for joins
     * @return projected stream
     */
    public ProjectedStream setUnidirectional(boolean isUnidirectional)
    {
        this.isUnidirectional = isUnidirectional;
        return this;
    }

    /**
     * Returns true if multiple data window shall be treated as a union.
     * @return retain union
     */
    public boolean isRetainUnion()
    {
        return isRetainUnion;
    }

    /**
     * Set to true to have multiple data window be treated as a union.
     * @param retainUnion indicator to union
     */
    public void setRetainUnion(boolean retainUnion)
    {
        isRetainUnion = retainUnion;
    }

    /**
     * Returns true if multiple data window shall be treated as an intersection.
     * @return retain intersection
     */
    public boolean isRetainIntersection()
    {
        return isRetainIntersection;
    }

    /**
     * Set to true to have multiple data window be treated as a intersection.
     * @param retainIntersection indicator to intersection
     */
    public void setRetainIntersection(boolean retainIntersection)
    {
        isRetainIntersection = retainIntersection;
    }

    /**
     * Renders the views onto the projected stream.
     * @param writer to render to
     * @param views to render
     */
    protected static void toEPLViews(StringWriter writer, List<View> views)
    {
        if ((views != null) && (views.size() != 0))
        {
            writer.write('.');
            String delimiter = "";
            for (View view : views)
            {
                writer.write(delimiter);
                view.toEPL(writer);
                delimiter = ".";
            }
        }
    }
}
