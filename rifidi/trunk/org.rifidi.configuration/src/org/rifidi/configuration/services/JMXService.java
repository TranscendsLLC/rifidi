package org.rifidi.configuration.services;

import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.mbeans.ConfigurationControlMBean;

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
