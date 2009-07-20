package org.rifidi.configuration;

import java.util.List;

import javax.management.MBeanInfo;

/**
 * ServiceFactories create new services using a map as their input. They have to
 * be registered to the service registry under this interface and the have to
 * carry a factoryname property.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface ServiceFactory<T> {

	/**
	 * Create a service with the given attributes. The service will be
	 * registered to OSGi.
	 * 
	 * @param factoryID
	 * @param serviceID
	 *            the id of the service to create, this will also be used in the
	 *            service param named serviceid
	 */
	void createInstance(String factoryID, String serviceID);

	/**
	 * Get a description for a service this factory creates.
	 * 
	 * @param factoryID
	 * @return
	 */
	MBeanInfo getServiceDescription(String factoryID);

	/**
	 * Get the list of factory ids this factory should register for.
	 */
	List<String> getFactoryIDs();
}
