/**
 * 
 */
package org.rifidi.configuration;

import java.util.Map;

import javax.management.DynamicMBean;

import org.osgi.framework.ServiceRegistration;

/**
 * Configurations provide a standard interface for handling services through a
 * configuration/management interface.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface Configuration extends DynamicMBean {

	/**
	 * Get the unique name of the configuration.
	 * 
	 * @return
	 */
	String getServiceID();

	/**
	 * Set the id for the governed service.
	 * 
	 */
	void setServiceID(String id);

	/**
	 * Get the id of the factory that registered the configuration.
	 * 
	 * @return
	 */
	String getFactoryID();

	/**
	 * Used for persistence. Should only be called when saving because it
	 * returns attributes as a Map<String,String>
	 */
	Map<String, String> getAttributes();

	/**
	 * Destroy the service and remove it from the registry.
	 */
	void destroy();

	/** Set the OSGi service registration for this object */
	void setServiceRegistration(ServiceRegistration registration);

	/**
	 * Get the names of all the DynamicMBean Properties. For use with the
	 * DynamicMBean getAttributes(String[]) method
	 * 
	 * @return An array of names of all the properties in this Configuration
	 */
	String[] getAttributeNames();
}
