/*
 *  Activator.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.rmi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.rmi.service.RMIServerService;
import org.rifidi.edge.rmi.service.impl.RMIServerServiceImpl;

// TODO Needs class comment header...
public class Activator implements BundleActivator {

	private RMIServerServiceImpl rmiServerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle RMI started ==");

		System.out.println("Registering Service: RMISeverService");
		rmiServerService = new RMIServerServiceImpl();
		context.registerService(RMIServerService.class.getName(),
				rmiServerService, null);

		rmiServerService.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		rmiServerService.stop();
		System.out.println("== Bundle RMI stopped ==");
	}

}
