/*
 * 
 * ConfigurationService.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.configuration.services;

import java.util.Set;

import javax.management.AttributeList;

import org.rifidi.edge.core.configuration.Configuration;
import org.rifidi.edge.core.exceptions.CannotCreateServiceException;

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
	void storeConfiguration();

	/**
	 * Get a configuration service by ID. Returns null if not avaialable
	 * 
	 * @param serviceID
	 *            The ID of the service
	 * @return The configuration with the given ID, or null if none is
	 *         available.
	 */
	Configuration getConfiguration(String serviceID);

	/**
	 * Create a new service.
	 * 
	 * @param factoryID The ID of the factory to use to create the new service
	 * @param attributes The Attributes to set on the new service
	 * @return The ID of the newly created service
	 */
	String createService(String factoryID, AttributeList attributes) throws CannotCreateServiceException;

	/**
	 * Destroy a service.
	 * 
	 * @param serviceID
	 */
	void destroyService(String serviceID);
	
	/**
	 * Get all configurations
	 * 
	 * @return The set of all available configurations
	 */
	public Set<Configuration> getConfigurations();
}
