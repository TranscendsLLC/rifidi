package org.rifidi.edge.core.daos;

import java.util.Set;

import org.rifidi.configuration.Configuration;

/**
 * A Data Access Object that listens for configurations on the osgi registry and
 * provides a standard way for other components to access them
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface ConfigurationDAO {

	/**
	 * Get a configuration service by ID. Returns null if not avaialable
	 * 
	 * @param serviceID
	 *            The ID of the service
	 * @return The configuration with the given ID, or null if none is
	 *         available.
	 */
	public Configuration getConfiguration(String serviceID);

	/**
	 * Get all configurations
	 * 
	 * @return The set of all available configurations
	 */
	public Set<Configuration> getConfigurations();
}
