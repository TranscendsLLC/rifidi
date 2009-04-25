/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.*;
import com.espertech.esper.epl.metric.MetricReportingPath;
import com.espertech.esper.epl.metric.MetricReportingService;
import com.espertech.esper.epl.named.NamedWindowService;
import com.espertech.esper.epl.spec.SelectClauseStreamSelectorEnum;
import com.espertech.esper.core.thread.ThreadingOption;
import com.espertech.esper.core.thread.ThreadingService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.vaevent.ValueAddEventService;
import com.espertech.esper.filter.FilterService;
import com.espertech.esper.plugin.PluginLoader;
import com.espertech.esper.schedule.SchedulingService;
import com.espertech.esper.timer.TimerService;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.util.SerializableObjectCopier;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Service provider encapsulates the engine's services for runtime and administration interfaces.
 */
public class EPServiceProviderImpl implements EPServiceProviderSPI
{
    private static final Log log = LogFactory.getLog(EPServiceProviderImpl.class);
    private volatile EPServiceEngine engine;
    private ConfigurationInformation configSnapshot;
    private String engineURI;
    private Set<EPServiceStateListener> serviceListeners;
    private Set<EPStatementStateListener> statementListeners;
    private StatementEventDispatcherUnthreaded stmtEventDispatcher;

    /**
     * Constructor - initializes services.
     * @param configuration is the engine configuration
     * @param engineURI is the engine URI or null if this is the default provider
     * @throws ConfigurationException is thrown to indicate a configuraton error
     */
    public EPServiceProviderImpl(Configuration configuration, String engineURI) throws ConfigurationException
    {
        if (configuration == null)
        {
            throw new NullPointerException("Unexpected null value received for configuration");
        }
        this.engineURI = engineURI;
        configSnapshot = takeSnapshot(configuration);
        serviceListeners = new CopyOnWriteArraySet<EPServiceStateListener>();
        statementListeners = new CopyOnWriteArraySet<EPStatementStateListener>();
        initialize();
    }

    /**
     * Sets engine configuration information for use in the next initialize.
     * @param configuration is the engine configs
     */
    public void setConfiguration(Configuration configuration)
    {
        configSnapshot = takeSnapshot(configuration);
    }

    public String getURI()
    {
        return engineURI;
    }

    public EPRuntime getEPRuntime()
    {
        return engine.getRuntime();
    }

    public EPAdministrator getEPAdministrator()
    {
        return engine.getAdmin();
    }

    public ThreadingService getThreadingService()
    {
        return engine.getServices().getThreadingService();
    }

    public EventAdapterService getEventAdapterService()
    {
        return engine.getServices().getEventAdapterService();
    }

    public SchedulingService getSchedulingService()
    {
        return engine.getServices().getSchedulingService();
    }

    public FilterService getFilterService()
    {
        return engine.getServices().getFilterService();
    }

    public TimerService getTimerService() {
        return engine.getServices().getTimerService();
    }

    public ConfigurationInformation getConfigurationInformation() {
        return configSnapshot;
    }

    public NamedWindowService getNamedWindowService()
    {
        return engine.getServices().getNamedWindowService();
    }

    public ExtensionServicesContext getExtensionServicesContext()
    {
        return engine.getServices().getExtensionServicesContext();
    }

    public StatementLifecycleSvc getStatementLifecycleSvc()
    {
        return engine.getServices().getStatementLifecycleSvc();
    }

    public MetricReportingService getMetricReportingService()
    {
        return engine.getServices().getMetricsReportingService();
    }

    public ValueAddEventService getValueAddEventService()
    {
        return engine.getServices().getValueAddEventService();
    }

    public StatementEventTypeRef getStatementEventTypeRef()
    {
        return engine.getServices().getStatementEventTypeRefService();
    }

    public Context getContext()
    {
        return engine.getServices().getEngineEnvContext();
    }

    public void destroy()
    {
        if (engine != null)
        {
            for (EPServiceStateListener listener : serviceListeners)
            {
                try
                {
                    listener.onEPServiceDestroyRequested(this);
                }
                catch (RuntimeException ex)
                {
                    log.error("Runtime exception caught during an onEPServiceDestroyRequested callback:" + ex.getMessage(), ex);
                }
            }

            engine.getServices().getTimerService().stopInternalClock(false);
            // Give the timer thread a little moment to catch up
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }

            engine.getRuntime().destroy();
            // plugin-loaders
            List<ConfigurationPluginLoader> pluginLoaders = configSnapshot.getPluginLoaders();
            for (ConfigurationPluginLoader config : pluginLoaders) {
                PluginLoader plugin = null;
                try {
                    plugin = (PluginLoader) engine.getServices().getEngineEnvContext().lookup("plugin-loader/" + config.getLoaderName());
                    plugin.destroy();
                }
                catch (NamingException e) {
                    // expected
                }
            }
            engine.getAdmin().destroy();
            engine.getServices().destroy();

            engine.getServices().initialize();
        }

        engine = null;
    }

    public boolean isDestroyed()
    {
        return engine == null;
    }

    public void initialize()
    {
        // This setting applies to all engines in a given VM
        ExecutionPathDebugLog.setDebugEnabled(configSnapshot.getEngineDefaults().getLogging().isEnableExecutionDebug());
        ExecutionPathDebugLog.setTimerDebugEnabled(configSnapshot.getEngineDefaults().getLogging().isEnableTimerDebug());

        // This setting applies to all engines in a given VM
        MetricReportingPath.setMetricsEnabled(configSnapshot.getEngineDefaults().getMetricsReporting().isEnableMetricsReporting());

        // This setting applies to all engines in a given VM
        ThreadingOption.setThreadingEnabled(ThreadingOption.isThreadingEnabled() ||
                configSnapshot.getEngineDefaults().getThreading().isThreadPoolTimerExec() ||
                configSnapshot.getEngineDefaults().getThreading().isThreadPoolInbound() ||
                configSnapshot.getEngineDefaults().getThreading().isThreadPoolRouteExec() ||
                configSnapshot.getEngineDefaults().getThreading().isThreadPoolOutbound());
        
        if (engine != null)
        {
            engine.getServices().getTimerService().stopInternalClock(false);
            // Give the timer thread a little moment to catch up
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }

            engine.getServices().destroy();
        }

        // Make EP services context factory
        String epServicesContextFactoryClassName = configSnapshot.getEPServicesContextFactoryClassName();
        EPServicesContextFactory epServicesContextFactory;
        if (epServicesContextFactoryClassName == null)
        {
            // Check system properties
            epServicesContextFactoryClassName = System.getProperty("ESPER_EPSERVICE_CONTEXT_FACTORY_CLASS");
        }
        if (epServicesContextFactoryClassName == null)
        {
            epServicesContextFactory = new EPServicesContextFactoryDefault();
        }
        else
        {
            Class clazz;
            try
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                clazz = Class.forName(epServicesContextFactoryClassName, true, cl);
            }
            catch (ClassNotFoundException e)
            {
                throw new ConfigurationException("Class '" + epServicesContextFactoryClassName + "' cannot be loaded");
            }

            Object obj;
            try
            {
                obj = clazz.newInstance();
            }
            catch (InstantiationException e)
            {
                throw new ConfigurationException("Class '" + clazz + "' cannot be instantiated");
            }
            catch (IllegalAccessException e)
            {
                throw new ConfigurationException("Illegal access instantiating class '" + clazz);
            }

            epServicesContextFactory = (EPServicesContextFactory) obj;
        }

        EPServicesContext services = epServicesContextFactory.createServicesContext(this, configSnapshot);

        // New runtime
        EPRuntimeImpl runtime = new EPRuntimeImpl(services);

        // Configure services to use the new runtime
        services.setInternalEventRouter(runtime);
        services.getTimerService().setCallback(runtime);

        // Statement lifycycle init
        services.getStatementLifecycleSvc().init();

        // New admin
        ConfigurationOperations configOps = new ConfigurationOperationsImpl(services.getEventAdapterService(), services.getEngineImportService(), services.getVariableService(), services.getEngineSettingsService(), services.getValueAddEventService(), services.getMetricsReportingService(), services.getStatementEventTypeRefService());
        SelectClauseStreamSelectorEnum defaultStreamSelector = SelectClauseStreamSelectorEnum.mapFromSODA(configSnapshot.getEngineDefaults().getStreamSelection().getDefaultStreamSelector());
        EPAdministratorImpl admin = new EPAdministratorImpl(services, configOps, defaultStreamSelector);

        // Start clocking
        if (configSnapshot.getEngineDefaults().getThreading().isInternalTimerEnabled())
        {
            services.getTimerService().startInternalClock();
        }

        // Give the timer thread a little moment to start up
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        // Save engine instance
        engine = new EPServiceEngine(services, runtime, admin);

        // Load and initialize adapter loader classes
        loadAdapters(services);

        // Initialize extension services
        if (services.getExtensionServicesContext() != null)
        {
            services.getExtensionServicesContext().init();
        }

        // Start metrics reporting, if any
        if (configSnapshot.getEngineDefaults().getMetricsReporting().isEnableMetricsReporting())
        {
            services.getMetricsReportingService().setContext(runtime, services);
        }

        // call initialize listeners
        for (EPServiceStateListener listener : serviceListeners)
        {
            try
            {
                listener.onEPServiceInitialized(this);
            }
            catch (RuntimeException ex)
            {
                log.error("Runtime exception caught during an onEPServiceInitialized callback:" + ex.getMessage(), ex);
            }
        }        
    }

    /**
     * Loads and initializes adapter loaders.
     * @param services is the engine instance services
     */
    private void loadAdapters(EPServicesContext services)
    {
        List<ConfigurationPluginLoader> pluginLoaders = configSnapshot.getPluginLoaders();
        if ((pluginLoaders == null) || (pluginLoaders.size() == 0))
        {
            return;
        }
        for (ConfigurationPluginLoader config : pluginLoaders)
        {
            String className = config.getClassName();
            Class pluginLoaderClass;
            try
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                pluginLoaderClass = Class.forName(className, true, cl);
            }
            catch (ClassNotFoundException ex)
            {
                throw new ConfigurationException("Failed to load adapter loader class '" + className + "'", ex);
            }

            Object pluginLoaderObj;
            try
            {
                pluginLoaderObj = pluginLoaderClass.newInstance();
            }
            catch (InstantiationException ex)
            {
                throw new ConfigurationException("Failed to instantiate adapter loader class '" + className + "' via default constructor", ex);
            }
            catch (IllegalAccessException ex)
            {
                throw new ConfigurationException("Illegal access to instantiate adapter loader class '" + className + "' via default constructor", ex);
            }

            PluginLoader pluginLoader = (PluginLoader) pluginLoaderObj;
            pluginLoader.init(config.getLoaderName(), config.getConfigProperties(), this);

            // register adapter loader in JNDI context tree
            try
            {
                services.getEngineEnvContext().bind("plugin-loader/" + config.getLoaderName(), pluginLoader);
            }
            catch (NamingException e)
            {
                throw new EPException("Failed to use context to bind adapter loader", e);
            }
        }
    }

    private static class EPServiceEngine
    {
        private EPServicesContext services;
        private EPRuntimeImpl runtime;
        private EPAdministratorImpl admin;

        public EPServiceEngine(EPServicesContext services, EPRuntimeImpl runtime, EPAdministratorImpl admin)
        {
            this.services = services;
            this.runtime = runtime;
            this.admin = admin;
        }

        public EPServicesContext getServices()
        {
            return services;
        }

        public EPRuntimeImpl getRuntime()
        {
            return runtime;
        }

        public EPAdministratorImpl getAdmin()
        {
            return admin;
        }
    }

    private ConfigurationInformation takeSnapshot(Configuration configuration)
    {
        try
        {
            return (ConfigurationInformation) SerializableObjectCopier.copy(configuration);
        }
        catch (IOException e)
        {
            throw new ConfigurationException("Failed to snapshot configuration instance through serialization : " + e.getMessage(), e);
        }
        catch (ClassNotFoundException e)
        {
            throw new ConfigurationException("Failed to snapshot configuration instance through serialization : " + e.getMessage(), e);
        }
    }

    public void addServiceStateListener(EPServiceStateListener listener)
    {
        serviceListeners.add(listener);
    }

    public boolean removeServiceStateListener(EPServiceStateListener listener)
    {
        return serviceListeners.remove(listener);
    }

    public void removeAllServiceStateListeners()
    {
        serviceListeners.clear();
    }

    public synchronized void addStatementStateListener(EPStatementStateListener listener)
    {
        if (statementListeners.isEmpty())
        {
            stmtEventDispatcher = new StatementEventDispatcherUnthreaded(this, statementListeners);
            this.getStatementLifecycleSvc().addObserver(stmtEventDispatcher);
        }
        statementListeners.add(listener);
    }

    public synchronized boolean removeStatementStateListener(EPStatementStateListener listener)
    {
        boolean result = statementListeners.remove(listener);
        if (statementListeners.isEmpty())
        {
            this.getStatementLifecycleSvc().removeObserver(stmtEventDispatcher);
            stmtEventDispatcher = null;
        }
        return result;
    }

    public synchronized void removeAllStatementStateListeners()
    {
        statementListeners.clear();
        if (statementListeners.isEmpty())
        {
            this.getStatementLifecycleSvc().removeObserver(stmtEventDispatcher);
            stmtEventDispatcher = null;
        }
    }
}
