/*
 * 
 * ProvisioningCommandProvider.java
 *  
 * Created:     Oct 1st, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.provisioning.impl;

import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.services.provisioning.ProvisioningService;
import org.rifidi.edge.core.services.provisioning.exceptions.CannotProvisionException;

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
