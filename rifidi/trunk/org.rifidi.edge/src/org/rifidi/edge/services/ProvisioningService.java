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
