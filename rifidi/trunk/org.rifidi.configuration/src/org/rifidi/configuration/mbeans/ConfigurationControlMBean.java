/**
 * 
 */
package org.rifidi.configuration.mbeans;

/**
 * Configuration management.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ConfigurationControlMBean {
	/**
	 * Save the current config.
	 */
	public void save();

	/**
	 * Reload configuration from file.
	 */
	public void reload();
}
