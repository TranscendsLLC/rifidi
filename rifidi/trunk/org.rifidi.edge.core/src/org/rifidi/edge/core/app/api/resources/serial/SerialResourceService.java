/**
 * 
 */
package org.rifidi.edge.core.app.api.resources.serial;

import org.rifidi.edge.core.app.api.resources.CannotCreateResourceException;
import org.rifidi.edge.core.app.api.resources.ResourceService;

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
	 * org.rifidi.edge.core.app.api.resources.ResourceService#createResource
	 * (org.rifidi.edge.core.app.api.resources.ResourceDescription)
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
