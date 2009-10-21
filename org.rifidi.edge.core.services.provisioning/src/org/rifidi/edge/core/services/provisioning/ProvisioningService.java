/*
 * 
 * ProvisioningService.java
 *  
 * Created:     Oct 1st, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */

package org.rifidi.edge.core.services.provisioning;

import java.util.Set;

import org.rifidi.edge.core.services.provisioning.exceptions.CannotProvisionException;

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
