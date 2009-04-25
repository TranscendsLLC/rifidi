/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.db;

import com.espertech.esper.epl.join.table.EventTable;

/**
 * Null implementation for a data cache that doesn't ever hit.
 */
public class DataCacheNullImpl implements DataCache
{
    public EventTable getCached(Object[] lookupKeys)
    {
        return null;
    }

    public void put(Object[] lookupKeys, EventTable rows)
    {

    }

    public boolean isActive()
    {
        return false;
    }
}
