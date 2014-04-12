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
package org.rifidi.edge.api.resources;

import java.util.HashMap;

/**
 * A ResourceService is a manager for integration resources, such as databases
 * and JMS. It creates and keeps track of created resources.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class ResourceService<D extends ResourceDescription, R> {

	/** The Map pf ResoruceDescription to Resoruces */
	private final HashMap<D, R> descriptionToResource = new HashMap<D, R>();

	/**
	 * Constructor
	 */
	public ResourceService() {

	}

	/**
	 * Clients use this method to get a hold of a resource
	 * 
	 * @param resourceDescription
	 *            The description of the resource to get
	 * @return A resource
	 * @throws CannotCreateResourceException
	 *             If there was a problem when creating the resource
	 */
	public synchronized R getResource(D resourceDescription)
			throws CannotCreateResourceException {

		if (!descriptionToResource.containsKey(resourceDescription)) {
			descriptionToResource.put(resourceDescription,
					createResource(resourceDescription));
		}
		return descriptionToResource.get(resourceDescription);
	}

	/**
	 * Subclasses should override this method to do the work of creating a
	 * resource
	 * 
	 * @param resourceDescription
	 *            The description of the resource to create
	 * @return The created resource
	 * @throws CannotCreateResourceException
	 */
	protected abstract R createResource(D resourceDescription)
			throws CannotCreateResourceException;

}
