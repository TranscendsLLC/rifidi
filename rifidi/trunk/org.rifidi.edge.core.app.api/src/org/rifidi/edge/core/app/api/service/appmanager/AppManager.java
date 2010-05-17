/**
 * 
 */
package org.rifidi.edge.core.app.api.service.appmanager;

import java.util.Set;

import org.rifidi.edge.core.app.api.RifidiApp;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface AppManager {

	void addApp(RifidiApp app);

	void start(String name);

	void start(RifidiApp app);

	void stop(String name);

	Set<String> getApps();

}
