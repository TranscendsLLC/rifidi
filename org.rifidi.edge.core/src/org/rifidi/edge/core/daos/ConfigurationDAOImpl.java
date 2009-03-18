/**
 * 
 */
package org.rifidi.edge.core.daos;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;

/**
 * This is the implementation that listens for OSGi services for Configurations.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ConfigurationDAOImpl implements ConfigurationDAO {

	/** The available Configurations */
	private Map<String, Configuration> configurations;
	private final static Log logger = LogFactory
			.getLog(ConfigurationDAOImpl.class);

	/**
	 * constructor
	 */
	public ConfigurationDAOImpl() {
		configurations = new HashMap<String, Configuration>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ConfigurationDAO#getConfiguration(java.
	 * lang.String)
	 */
	@Override
	public Configuration getConfiguration(String serviceID) {
		return configurations.get(serviceID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.daos.ConfigurationDAO#getConfigurations()
	 */
	@Override
	public Set<Configuration> getConfigurations() {
		return new HashSet<Configuration>(configurations.values());
	}

	/**
	 * Used by spring to bind a new Configuration to this service.
	 * 
	 * @param Configuration
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindConfiguration(Configuration configuration,
			Dictionary<String, String> parameters) {
		logger.info("config bound: " + configuration.getServiceID());
		configurations.put(configuration.getServiceID(), configuration);
	}

	/**
	 * Used by spring to unbind a disappearing Configuration service from this
	 * service.
	 * 
	 * @param configuration
	 *            the Configuration to unbind
	 * @param parameters
	 */
	public void unbindConfiguration(Configuration configuration,
			Dictionary<String, String> parameters) {
		logger.info("config unbound: " + configuration.getServiceID());
		configurations.remove(configuration.getServiceID());
	}

	/**
	 * Used by spring to give the initial list of readerSession configuration
	 * 
	 * 
	 * @param configurations
	 *            the initial list of available configurations
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		for (Configuration configuration : configurations) {
			this.configurations
					.put(configuration.getServiceID(), configuration);
		}
	}

}
