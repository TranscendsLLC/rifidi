/**
 * 
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
