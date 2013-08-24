/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
