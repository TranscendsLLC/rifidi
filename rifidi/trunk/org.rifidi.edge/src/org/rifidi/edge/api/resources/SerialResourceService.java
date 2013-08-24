/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.resources;


/**
 * This class creates a serial resource.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SerialResourceService extends
		ResourceService<SerialResourceDescription, SerialResource> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.resources.ResourceService#createResource
	 * (org.rifidi.edge.api.resources.ResourceDescription)
	 */
	@Override
	protected SerialResource createResource(
			SerialResourceDescription resourceDescription)
			throws CannotCreateResourceException {
		try {
			SerialResource serialResource = new SerialResource();
			serialResource.setCommPortName(resourceDescription.getPort());
			serialResource.setBaud(resourceDescription.getBaud());
			serialResource.setDatabits(resourceDescription.getDatabits());
			serialResource.setParity(resourceDescription.getParity());
			serialResource.setStopbits(resourceDescription.getStopbits());
			return serialResource;
		} catch (Exception e) {
			throw new CannotCreateResourceException(e);
		}
	}

}
