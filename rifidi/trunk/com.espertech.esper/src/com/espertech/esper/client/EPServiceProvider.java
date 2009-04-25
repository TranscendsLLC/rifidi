/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

import javax.naming.Context;

/**
 * This class provides access to the EPRuntime and EPAdministrator implementations.
 */
public interface EPServiceProvider
{
    /**
     * Returns a class instance of EPRuntime.
     * @return an instance of EPRuntime
     */
    public EPRuntime getEPRuntime();

    /**
     * Returns a class instance of EPAdministrator.
     * @return an instance of EPAdministrator
     */
    public EPAdministrator getEPAdministrator();

    /**
     * Provides naming context for public named objects.
     * <p>
     * An extension point designed for use by input and output adapters as well as
     * other extension services.
     * @return naming context providing name-to-object bindings
     */
    public Context getContext();

    /**
     * Frees any resources associated with this engine instance, and leaves the engine instance
     * ready for further use.
     * <p>
     * Retains the existing configuration of the engine instance but forgets any runtime configuration changes.
     * <p>
     * Stops and destroys any existing statement resources such as filters, patterns, expressions, views.
     */
    public void initialize();

    /**
     * Returns the provider URI, or null if this is the default provider.
     * @return provider URI
     */
    public String getURI();

    /**
     * Destroys the service.
     * <p>
     * Releases any resources held by the service. The service enteres a state in
     * which operations provided by administrative and runtime interfaces originiated by the service
     * are not guaranteed to operate properly.
     * <p>
     * Removes the service URI from the known URIs. Allows configuration to change for the instance.
     */
    public void destroy();

    /**
     * Returns true if the service is in destroyed state, or false if not.
     * @return indicator whether the service has been destroyed
     */
    public boolean isDestroyed();

    /**
     * Add a listener to service provider state changes that receives a before-destroy event.
     * The listener collection applies set-semantics.
     * @param listener to add
     */
    public void addServiceStateListener(EPServiceStateListener listener);

    /**
     * Removate a listener to service provider state changes.
     * @param listener to remove
     * @return true to indicate the listener was removed, or fals
     */
    public boolean removeServiceStateListener(EPServiceStateListener listener);

    /**
     * Remove all listeners to service provider state changes.
     */
    public void removeAllServiceStateListeners();

    /**
     * Add a listener to statement state changes that receives statement-level events.
     * The listener collection applies set-semantics.
     * @param listener to add
     */
    public void addStatementStateListener(EPStatementStateListener listener);

    /**
     * Removate a listener to statement state changes.
     * @param listener to remove
     * @return true to indicate the listener was removed, or fals
     */
    public boolean removeStatementStateListener(EPStatementStateListener listener);

    /**
     * Remove all listeners to statement state changes.
     */
    public void removeAllStatementStateListeners();
}
