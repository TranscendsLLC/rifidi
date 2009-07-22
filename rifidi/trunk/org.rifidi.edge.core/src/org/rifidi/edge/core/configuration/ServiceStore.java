/**
 * 
 */
package org.rifidi.edge.core.configuration;

import java.util.Map;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@XmlType
public class ServiceStore {
	private String factoryID;
	private String serviceID;
	private ConfigurationType type;
	private Map<String, String> attributes;

	/**
	 * @return the factoryID
	 */
	public String getFactoryID() {
		return factoryID;
	}

	/**
	 * @param factoryID
	 *            the factoryID to set
	 */
	public void setFactoryID(String factoryID) {
		this.factoryID = factoryID;
	}

	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}

	/**
	 * @param serviceID
	 *            the serviceID to set
	 */
	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the type
	 */
	public ConfigurationType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ConfigurationType type) {
		this.type = type;
	}

}
