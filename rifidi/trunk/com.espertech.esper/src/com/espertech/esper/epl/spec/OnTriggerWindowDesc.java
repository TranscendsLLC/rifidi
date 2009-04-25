/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

/**
 * Specification for the on-delete statement.
 */
public class OnTriggerWindowDesc extends OnTriggerDesc
{
    private String windowName;
    private String optionalAsName;

    /**
     * Ctor.
     * @param windowName the window name
     * @param optionalAsName the optional name
     * @param isOnDelete true for on-delete and false for on-select
     */
    public OnTriggerWindowDesc(String windowName, String optionalAsName, boolean isOnDelete)
    {
        super(isOnDelete ? OnTriggerType.ON_DELETE : OnTriggerType.ON_SELECT);
        this.windowName = windowName;
        this.optionalAsName = optionalAsName;
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
     * Returns the name, or null if none defined.
     * @return name
     */
    public String getOptionalAsName()
    {
        return optionalAsName;
    }
}
