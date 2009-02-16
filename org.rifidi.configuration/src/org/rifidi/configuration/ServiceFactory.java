/**
 * 
 */
package org.rifidi.configuration;

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
	 * Create a service from the given Configuration.
	 * 
	 * @param configuration
	 */
	void createService(Configuration configuration);

	/**
	 * Get the unique ID for this factory.
	 */
	String getFactoryID();

	/**
	 * Create an empty configuration object for the factory.
	 * 
	 * @return
	 */
	public Configuration getEmptyConfiguration();
}
