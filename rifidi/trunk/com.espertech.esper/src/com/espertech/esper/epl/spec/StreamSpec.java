/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.spec.ViewSpec;
import java.util.List;

/**
 * Specification for a stream, consists simply of an optional stream name and a list of views
 * on to of the stream.
 * <p>
 * Implementation classes for views and patterns add additional information defining the
 * stream of events.
 */
public interface StreamSpec
{
    /**
     * Returns the stream name, or null if undefined.
     * @return stream name
     */
    public String getOptionalStreamName();

    /**
     * Returns views definitions onto the stream
     * @return view defs
     */
    public List<ViewSpec> getViewSpecs();

    /**
     * Returns the options for the stream such as unidirectional, retain-union etc.
     * @return stream options
     */    
    public StreamSpecOptions getOptions();
}
