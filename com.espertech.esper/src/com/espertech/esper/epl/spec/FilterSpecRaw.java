/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.util.MetaDefItem;

import java.util.List;
import java.io.Serializable;

/**
 * Filter definition in an un-validated and un-resolved form.
 * <p>
 * Event type and expression nodes in this filter specification are not yet validated, optimized for resolved
 * against actual streams.
 */
public class FilterSpecRaw implements MetaDefItem, Serializable
{
    private String eventTypeName;
    private List<ExprNode> filterExpressions;
    private PropertyEvalSpec optionalPropertyEvalSpec;
    private static final long serialVersionUID = 4316000245281974225L;

    /**
     * Ctor.
     * @param eventTypeName is the name of the event type
     * @param filterExpressions is a list of expression nodes representing individual filter expressions
     * @param optionalPropertyEvalSpec specification for a property select
     */
    public FilterSpecRaw(String eventTypeName, List<ExprNode> filterExpressions, PropertyEvalSpec optionalPropertyEvalSpec)
    {
        this.eventTypeName = eventTypeName;
        this.filterExpressions = filterExpressions;
        this.optionalPropertyEvalSpec = optionalPropertyEvalSpec;
    }

    /**
     * Default ctor.
     */
    public FilterSpecRaw()
    {
    }

    /**
     * Returns the event type name of the events we are looking for.
     * @return event name
     */
    public String getEventTypeName()
    {
        return eventTypeName;
    }

    /**
     * Returns the list of filter expressions.
     * @return filter expression list
     */
    public List<ExprNode> getFilterExpressions()
    {
        return filterExpressions;
    }

    /**
     * Returns the property evaluation specification, if any, or null if no properties evaluated.
     * @return property eval spec
     */
    public PropertyEvalSpec getOptionalPropertyEvalSpec()
    {
        return optionalPropertyEvalSpec;
    }
}
