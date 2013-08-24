/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.configuration;

import javax.management.MBeanInfo;

import org.rifidi.edge.exceptions.InvalidStateException;

/**
 * ServiceFactories create new services using a map as their input. They have to
 * be registered to the service registry under this interface and the have to
 * carry a factoryname property.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface ServiceFactory<T> {

	/**
	 * Create a service with the given attributes. The service will be
	 * registered to OSGi.
	 * 
	 * @param serviceID
	 *            the id of the service to create, this will also be used in the
	 *            service param named serviceid
	 * @exception IllegalArgumentException
	 *                If the service ID is invalid
	 * @exception InvalidStateException
	 *                If service factory is not in a state to create an instance
	 *                of the service
	 */
	void createInstance(String serviceID) throws IllegalArgumentException,
			InvalidStateException;

	/**
	 * Get a description for a service this factory creates.
	 * 
	 * @param factoryID
	 * @return
	 */
	MBeanInfo getServiceDescription(String factoryID);

	/**
	 * Get the id of this factory.
	 * 
	 * @return
	 */
	String getFactoryID();
}
