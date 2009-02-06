/**
 * 
 */
package org.rifidi.configuration;

import java.util.Map;

import javax.management.DynamicMBean;

import org.rifidi.configuration.mbeans.RifidiDynamicMBean;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface PersistentService {
	/**
	 * Get the map containing the parameters for this service. Must not return
	 * null.
	 * 
	 * @return
	 */
	public Map<String, String> getConfiguration();

	/**
	 * Get the JMX management object for the service.
	 * 
	 * @return
	 */
	public RifidiDynamicMBean getRifidiDynamicMBean();
}
