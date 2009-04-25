/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.plugin;

import com.espertech.esper.core.EPServiceProviderSPI;

import java.util.Properties;

/**
 * Interface for loaders of input/output adapters.
 */
public interface PluginLoader
{
    /**
     * Initializes the adapter loader.
     * @param name is the loader name
     * @param properties is a set of properties from the configuration
     * @param epService is the SPI of the engine itself for sending events to
     */
    public void init(String name, Properties properties, EPServiceProviderSPI epService);

    /**
     * Destroys adapter loader and adapters loaded.
     */
    public void destroy();
}
