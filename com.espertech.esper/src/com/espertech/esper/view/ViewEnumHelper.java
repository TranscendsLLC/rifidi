/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

import com.espertech.esper.epl.spec.PluggableObjectCollection;
import com.espertech.esper.epl.spec.PluggableObjectType;

/**
 * Helper producing a repository of built-in views.
 */
public class ViewEnumHelper
{
    private final static PluggableObjectCollection builtinViews;

    static
    {
        builtinViews = new PluggableObjectCollection();
        for (ViewEnum viewEnum : ViewEnum.values())
        {
            builtinViews.addObject(viewEnum.getNamespace(), viewEnum.getName(), viewEnum.getFactoryClass(), PluggableObjectType.VIEW);
        }
    }

    /**
     * Returns a collection of plug-in views.
     * @return built-in view definitions
     */
    public static PluggableObjectCollection getBuiltinViews()
    {
        return builtinViews;
    }
}
