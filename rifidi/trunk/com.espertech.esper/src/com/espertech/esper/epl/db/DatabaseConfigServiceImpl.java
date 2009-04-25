/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.db;

import com.espertech.esper.client.ConfigurationDBRef;
import com.espertech.esper.client.ConfigurationDataCache;
import com.espertech.esper.schedule.SchedulingService;
import com.espertech.esper.schedule.ScheduleBucket;
import com.espertech.esper.core.EPStatementHandle;

import java.util.Map;
import java.util.HashMap;

/**
 * Implementation provides database instance services such as connection factory and
 * connection settings.
 */
public class DatabaseConfigServiceImpl implements DatabaseConfigService
{
    private final Map<String, ConfigurationDBRef> mapDatabaseRef;
    private final Map<String, DatabaseConnectionFactory> connectionFactories;
    private final SchedulingService schedulingService;
    private final ScheduleBucket scheduleBucket;

    /**
     * Ctor.
     * @param mapDatabaseRef is a map of database name and database configuration entries
     * @param schedulingService is for scheduling callbacks for a cache
     * @param scheduleBucket is a system bucket for all scheduling callbacks for caches
     */
    public DatabaseConfigServiceImpl(Map<String, ConfigurationDBRef> mapDatabaseRef,
                                     SchedulingService schedulingService,
                                     ScheduleBucket scheduleBucket)
    {
        this.mapDatabaseRef = mapDatabaseRef;
        this.connectionFactories = new HashMap<String, DatabaseConnectionFactory>();
        this.schedulingService = schedulingService;
        this.scheduleBucket = scheduleBucket;
    }

    public ConnectionCache getConnectionCache(String databaseName, String preparedStatementText) throws DatabaseConfigException
    {
        ConfigurationDBRef config = mapDatabaseRef.get(databaseName);
        if (config == null)
        {
            throw new DatabaseConfigException("Cannot locate configuration information for database '" + databaseName + '\'');
        }

        DatabaseConnectionFactory connectionFactory = getConnectionFactory(databaseName);

        boolean retain = config.getConnectionLifecycleEnum().equals(ConfigurationDBRef.ConnectionLifecycleEnum.RETAIN);
        if (retain)
        {
            return new ConnectionCacheImpl(connectionFactory, preparedStatementText);
        }
        else
        {
            return new ConnectionNoCacheImpl(connectionFactory, preparedStatementText);
        }
    }

    public DatabaseConnectionFactory getConnectionFactory(String databaseName) throws DatabaseConfigException
    {
        // check if we already have a reference
        DatabaseConnectionFactory factory = connectionFactories.get(databaseName);
        if (factory != null)
        {
            return factory;
        }

        ConfigurationDBRef config = mapDatabaseRef.get(databaseName);
        if (config == null)
        {
            throw new DatabaseConfigException("Cannot locate configuration information for database '" + databaseName + '\'');
        }

        ConfigurationDBRef.ConnectionSettings settings = config.getConnectionSettings();
        if (config.getConnectionFactoryDesc() instanceof ConfigurationDBRef.DriverManagerConnection)
        {
            ConfigurationDBRef.DriverManagerConnection dmConfig = (ConfigurationDBRef.DriverManagerConnection) config.getConnectionFactoryDesc();
            factory = new DatabaseDMConnFactory(dmConfig, settings);
        }
        else if (config.getConnectionFactoryDesc() instanceof ConfigurationDBRef.DataSourceConnection)
        {
            ConfigurationDBRef.DataSourceConnection dsConfig = (ConfigurationDBRef.DataSourceConnection) config.getConnectionFactoryDesc();
            factory = new DatabaseDSConnFactory(dsConfig, settings);
        }
        else if (config.getConnectionFactoryDesc() instanceof ConfigurationDBRef.DataSourceFactory)
        {
            ConfigurationDBRef.DataSourceFactory dsConfig = (ConfigurationDBRef.DataSourceFactory) config.getConnectionFactoryDesc();
            factory = new DatabaseDSFactoryConnFactory(dsConfig, settings);
        }
        else if (config.getConnectionFactoryDesc() == null)
        {
            throw new DatabaseConfigException("No connection factory setting provided in configuration");
        }
        else
        {
            throw new DatabaseConfigException("Unknown connection factory setting provided in configuration");
        }

        connectionFactories.put(databaseName, factory);

        return factory;
    }

    public DataCache getDataCache(String databaseName, EPStatementHandle epStatementHandle) throws DatabaseConfigException
    {
        ConfigurationDBRef config = mapDatabaseRef.get(databaseName);
        if (config == null)
        {
            throw new DatabaseConfigException("Cannot locate configuration information for database '" + databaseName + '\'');
        }

        ConfigurationDataCache dataCacheDesc = config.getDataCacheDesc();
        return DataCacheFactory.getDataCache(dataCacheDesc, epStatementHandle, schedulingService, scheduleBucket);
    }

    public ColumnSettings getQuerySetting(String databaseName) throws DatabaseConfigException
    {
        ConfigurationDBRef config = mapDatabaseRef.get(databaseName);
        if (config == null)
        {
            throw new DatabaseConfigException("Cannot locate configuration information for database '" + databaseName + '\'');
        }
        return new ColumnSettings(config.getMetadataRetrievalEnum(), config.getColumnChangeCase(), config.getSqlTypesMapping());
    }
}
