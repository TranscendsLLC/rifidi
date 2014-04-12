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

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * This class provides commands to the equinox console to control the
 * provisioning service
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ProvisioningCommandProvider implements CommandProvider {

	/** The Provisioning Service */
	private volatile ProvisioningService provisioningService;

	/**
	 * Called by spring
	 * 
	 * @param provisioningService
	 */
	public void setProvisioningService(ProvisioningService provisioningService) {
		this.provisioningService = provisioningService;
	}

	/**
	 * 
	 * @param intp
	 * @return
	 */
	public Object _loadApp(CommandInterpreter intp) {
		String path = intp.nextArgument();
		if (path == null) {
			intp.println("You must supply an application");
		}
		try {
			Set<String> bundles = provisioningService.provision(path);
			for (String s : bundles) {
				intp.println("Bundle added: " + s);
			}
		} catch (CannotProvisionException e) {
			intp.println("Cannot Load Application");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Rifidi Edge Server Provisioning---\n");
		buffer.append("\tloadApp <Application Name> - load an application\n");
		return buffer.toString();
	}

}
