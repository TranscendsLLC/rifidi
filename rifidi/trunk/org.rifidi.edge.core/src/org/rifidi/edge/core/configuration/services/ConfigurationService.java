package org.rifidi.edge.core.configuration.services;

import java.util.Set;

import javax.management.AttributeList;

import org.rifidi.edge.core.configuration.Configuration;

/**
 * Services implementing this interface are supposed to provide a link between
 * custom properties/preference storage (ini files, xml) and the OSGi config
 * admin service.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ConfigurationService {
	/**
	 * Store the all configurations of persistent services.
	 */
	void storeConfiguration();

	/**
	 * Get a configuration service by ID. Returns null if not avaialable
	 * 
	 * @param serviceID
	 *            The ID of the service
	 * @return The configuration with the given ID, or null if none is
	 *         available.
	 */
	Configuration getConfiguration(String serviceID);

	/**
	 * Create a new service.
	 * 
	 * @param factoryID
	 * @param attributes
	 */
	void createService(String factoryID, AttributeList attributes);

	/**
	 * Destroy a service.
	 * 
	 * @param serviceID
	 */
	void destroyService(String serviceID);
	
	/**
	 * Get all configurations
	 * 
	 * @return The set of all available configurations
	 */
	public Set<Configuration> getConfigurations();
}