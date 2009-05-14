package org.rifidi.edge.core.daos;

import java.util.Set;

import org.rifidi.configuration.Configuration;

/**
 * This service listens for configurations on the osgi registry.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface ConfigurationDAO {

	/**
	 * TODO: Method level comment.
	 * 
	 * @param serviceID
	 * @return
	 */
	public Configuration getConfiguration(String serviceID);

	/**
	 * TODO: Method level comment.
	 * 
	 * @return
	 */
	public Set<Configuration> getConfigurations();
}
