/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.variable;

import java.util.Map;

/**
 * Thread-specific state in regards to variable versions.
 */
public class VariableVersionThreadEntry
{
    private Integer version;
    private Map<Integer, Object> uncommitted;

    /**
     * Ctor.
     * @param version current version number of the variables visible to thread
     * @param uncommitted the uncommitted values of variables for the thread, if any
     */
    public VariableVersionThreadEntry(int version, Map<Integer, Object> uncommitted)
    {
        this.version = version;
        this.uncommitted = uncommitted;
    }

    /**
     * Returns the version visible for a thread.
     * @return version number
     */
    public Integer getVersion()
    {
        return version;
    }

    /**
     * Sets the version visible for a thread.
     * @param version version number
     */
    public void setVersion(Integer version)
    {
        this.version = version;
    }

    /**
     * Returns a map of variable number and uncommitted value, or empty map or null if none exist
     * @return uncommitted values
     */
    public Map<Integer, Object> getUncommitted()
    {
        return uncommitted;
    }

    /**
     * Sets a map of variable number and uncommitted value, or empty map or null if none exist
     * @param uncommitted uncommitted values
     */
    public void setUncommitted(Map<Integer, Object> uncommitted)
    {
        this.uncommitted = uncommitted;
    }
}
