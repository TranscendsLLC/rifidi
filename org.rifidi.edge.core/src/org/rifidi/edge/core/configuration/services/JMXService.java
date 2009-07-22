package org.rifidi.edge.core.configuration.services;

import org.rifidi.edge.core.configuration.Configuration;
import org.rifidi.edge.core.configuration.mbeans.ConfigurationControlMBean;

/**
 * Interface for a JMX Service to implement
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface JMXService {
	void publish(Configuration config);

	void unpublish(Configuration config);

	/**
	 * Set the reference to the configuration control mbean.
	 * 
	 * @param mbean
	 */
	public void setConfigurationControlMBean(ConfigurationControlMBean mbean);

}
