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
 * Enum for the type of on-trigger statement.
 */
public enum OnTriggerType
{
    /**
     * For on-delete triggers that delete from a named window when a triggering event arrives.
     */
    ON_DELETE,

    /**
     * For on-select triggers that selected from a named window when a triggering event arrives.
     */
    ON_SELECT,

    /**
     * For on-set triggers that set variable values when a triggering event arrives.
     */
    ON_SET
}
