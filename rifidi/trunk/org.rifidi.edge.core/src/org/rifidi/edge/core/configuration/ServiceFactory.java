package org.rifidi.edge.core.configuration;

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
	 * @param serviceID
	 *            the id of the service to create, this will also be used in the
	 *            service param named serviceid
	 */
	void createInstance(String serviceID);

	/**
	 * Get a description for a service this factory creates.
	 * 
	 * @param factoryID
	 * @return
	 */
	MBeanInfo getServiceDescription(String factoryID);

	/**
	 * Get the id of this factory.
	 * 
	 * @return
	 */
	String getFactoryID();
}
