/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.epl.spec.PluggableObjectCollection;
import com.espertech.esper.epl.spec.PluggableObjectType;
import com.espertech.esper.pattern.guard.GuardEnum;
import com.espertech.esper.pattern.observer.ObserverEnum;

/**
 * Helper producing a repository of built-in pattern objects.
 */
public class PatternObjectHelper
{
    private final static PluggableObjectCollection builtinPatternObjects;

    static
    {
        builtinPatternObjects = new PluggableObjectCollection();
        for (GuardEnum guardEnum : GuardEnum.values())
        {
            builtinPatternObjects.addObject(guardEnum.getNamespace(), guardEnum.getName(), guardEnum.getClazz(), PluggableObjectType.PATTERN_GUARD);
        }
        for (ObserverEnum observerEnum : ObserverEnum.values())
        {
            builtinPatternObjects.addObject(observerEnum.getNamespace(), observerEnum.getName(), observerEnum.getClazz(), PluggableObjectType.PATTERN_OBSERVER);
        }
    }

    /**
     * Returns the built-in pattern objects.
     * @return collection of built-in pattern objects.
     */
    public static PluggableObjectCollection getBuiltinPatternObjects()
    {
        return builtinPatternObjects;
    }
}
