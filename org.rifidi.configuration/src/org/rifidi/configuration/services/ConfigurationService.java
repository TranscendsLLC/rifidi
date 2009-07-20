package org.rifidi.configuration.services;

import java.util.Set;

import javax.management.AttributeList;

import org.rifidi.configuration.Configuration;

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
	 * Add a new configuration to the service.
	 * 
	 * @param serviceID
	 * @param instance
	 * @param attributes
	 */
	void createService(String factoryID, AttributeList attributes);

	/**
	 * Get all configurations
	 * 
	 * @return The set of all available configurations
	 */
	public Set<Configuration> getConfigurations();
}
