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

import java.util.List;

/**
 * Specification for a pattern guard object consists of a namespace, name and guard object parameters.
 */
public final class PatternGuardSpec extends ObjectSpec
{
    /**
     * Constructor.
     * @param namespace if the namespace the object is in
     * @param objectName is the name of the object
     * @param objectParameters is a list of values representing the object parameters
     */
    public PatternGuardSpec(String namespace, String objectName, List<ExprNode> objectParameters)
    {
        super(namespace, objectName, objectParameters);
    }
}
