/**
 * 
 */
package org.rifidi.configuration.services;

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
	public void storeConfiguration();
}
