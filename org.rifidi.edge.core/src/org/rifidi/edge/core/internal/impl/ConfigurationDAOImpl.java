/**
 * 
 */
package org.rifidi.edge.core.internal.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.internal.ConfigurationDAO;

/**
 * @author kyle
 * 
 */
public class ConfigurationDAOImpl implements ConfigurationDAO {

	/** The available Configurations */
	private Set<Configuration> configurations;

	/**
	 * constructor
	 */
	public ConfigurationDAOImpl() {
		configurations = new HashSet<Configuration>();
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
		// TODO Auto-generated method stub
		Iterator<Configuration> iter = configurations.iterator();
		while (iter.hasNext()) {
			Configuration config = iter.next();
			if (config.getServiceID().equals(serviceID)) {
				return config;
			}
		}
		return null;
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
		configurations.add(configuration);
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
		configurations.remove(configuration);
	}

	/**
	 * Used by spring to give the initial list of reader configuration
	 * 
	 * 
	 * @param configurations
	 *            the initial list of available configurations
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		this.configurations.addAll(configurations);
	}

}
