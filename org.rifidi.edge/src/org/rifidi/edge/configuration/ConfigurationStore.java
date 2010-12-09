/*
 * 
 * ConfigurationStore.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.configuration;

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
