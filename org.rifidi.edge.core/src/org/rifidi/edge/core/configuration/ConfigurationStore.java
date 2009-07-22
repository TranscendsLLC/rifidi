/**
 * 
 */
package org.rifidi.edge.core.configuration;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@XmlRootElement
public class ConfigurationStore {
	private List<ServiceStore> services;

	/**
	 * @return the services
	 */
	public List<ServiceStore> getServices() {
		return services;
	}

	/**
	 * @param services
	 *            the services to set
	 */
	public void setServices(List<ServiceStore> services) {
		this.services = services;
	}

}
