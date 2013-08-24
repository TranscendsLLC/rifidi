/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.services;

import java.util.Set;


/**
 * This service allows you to provision the current running edge server using
 * OBR
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ProvisioningService {

	/**
	 * Look at an XML file produced by bindex and try to bring in every resource
	 * in that file
	 * 
	 * @param application
	 *            The name of the folder which contains the application to
	 *            install.
	 * @throws CannotProvisionException
	 *             If there was a problem when trying to bring in the resources
	 * 
	 * @return The IDs of the bundles that were added to the currently running
	 *         system
	 */
	Set<String> provision(String application) throws CannotProvisionException;

}
