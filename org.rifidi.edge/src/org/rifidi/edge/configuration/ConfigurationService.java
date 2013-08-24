/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.configuration;

import java.util.Set;

import javax.management.AttributeList;

import org.rifidi.edge.exceptions.CannotCreateServiceException;

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
