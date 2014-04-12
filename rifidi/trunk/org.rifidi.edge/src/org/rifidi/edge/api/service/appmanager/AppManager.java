/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
