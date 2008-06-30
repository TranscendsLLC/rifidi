/*
 *  Activator.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.core.service.communication.CommunicationService;

/**
 * @author jerry
 * 
 */
public class Activator implements BundleActivator {

	private CommunicationServiceImpl communicationService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");

		System.out.println("Registering Service: CommunicationService");

		communicationService = new CommunicationServiceImpl();
		context.registerService(CommunicationService.class.getName(),
				communicationService, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " stopped ==");
	}

}
