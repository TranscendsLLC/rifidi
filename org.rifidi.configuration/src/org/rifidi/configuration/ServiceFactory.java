
package org.rifidi.configuration;

import java.util.List;

/**
 * ServiceFactories create new services using a map as their input. They have to
 * be registered to the service registry under this interface and the have to
 * carry a factoryname property.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface ServiceFactory {

	/**
	 * Create a service from the given Configuration.
	 * 
	 * @param configuration
	 */
	void createService(Configuration configuration);

	/**
	 * Get the list of factory ids this factory should register for.
	 */
	List<String> getFactoryIDs();

	/**
	 * Create an empty configuration object for the given factory id.
	 * 
	 * @param factoryID
	 * @return
	 */
	public Configuration getEmptyConfiguration(String factoryID);
}
