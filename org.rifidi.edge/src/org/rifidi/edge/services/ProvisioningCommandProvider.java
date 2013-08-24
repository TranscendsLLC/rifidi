/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
