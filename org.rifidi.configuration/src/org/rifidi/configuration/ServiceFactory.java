/**
 * 
 */
package org.rifidi.configuration;

import java.util.Map;

/**
 * ServiceFactories create new services using a map as their input. They have to
 * be registered to the service registry under this interface and the have to
 * carry a factoryname property.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ServiceFactory {

	/**
	 * Create a service with the given properties and register it to OSGi under
	 * the given id.
	 * 
	 * @param serviceID
	 * @param properties
	 */
	void createService(String serviceID, Map<String, String> properties);

	/**
	 * Create a service with the given properties.
	 * 
	 * @param properties
	 */
	void createService(Map<String, String> properties);

	/**
	 * Get the unique ID for this factory.
	 */
	String getFactoryID();
}
