/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

/**
 * Observer statement management events.
 */
public interface StatementLifecycleObserver
{
    /**
     * Observer statement state changes.
     * @param event indicates statement changed
     */
    public void observe(StatementLifecycleEvent event);
}
