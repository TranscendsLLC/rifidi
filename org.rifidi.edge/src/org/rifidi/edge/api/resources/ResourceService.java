/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
