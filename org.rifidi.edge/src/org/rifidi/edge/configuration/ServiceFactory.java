/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
