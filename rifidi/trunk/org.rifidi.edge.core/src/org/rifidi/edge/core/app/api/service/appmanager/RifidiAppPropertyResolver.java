/**
 * 
 */
package org.rifidi.edge.core.app.api.service.appmanager;

import java.util.Properties;

/**
 * This interface is for classes that resolve properties for a given app.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiAppPropertyResolver {

	/**
	 * Resolve the property for the given app
	 * 
	 * @param appGroup
	 * @param appName
	 * @return
	 */
	Properties reolveProperties(String appGroup, String appName);

}
