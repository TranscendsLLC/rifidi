/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.appmanager;

import java.util.Map;

import org.rifidi.edge.api.RifidiApp;

/**
 * The AppManager keeps track of all registered Rifidi Apps. Apps are loaded
 * into the Application Manager by registering themselves in the OSGi registry
 * under the org.rifidi.edge.app.api.RifidiApp interface
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface AppManager {

	/**
	 * Dynamically load a group of applications into the edge server from an
	 * OSGi bundle.
	 * 
	 * @param groupName
	 *            The name of the group.
	 */
	void loadGroup(String groupName);

	/**
	 * Start an application with the given ID
	 * 
	 * @param appID
	 */
	void startApp(Integer appID);

	/**
	 * Stop an application with the given ID
	 * 
	 * @param appID
	 */
	void stopApp(Integer appID);

	/**
	 * Return all Rifidi Applications that have been loaded
	 * 
	 * @return
	 */
	Map<Integer, RifidiApp> getApps();

}
