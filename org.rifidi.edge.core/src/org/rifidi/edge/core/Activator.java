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
package org.rifidi.edge.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.service.readerconnection.impl.ReaderConnectionRegistryServiceImpl;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;
import org.rifidi.edge.core.service.readerplugin.impl.ReaderPluginRegistryServiceImpl;

public class Activator implements BundleActivator {

	private ReaderConnectionRegistryService connectionService;
	private ReaderPluginRegistryService readerPluginRegistryService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");

		System.out.println("Registering Service: "
				+ "ReaderPluginRegistryService");
		readerPluginRegistryService = new ReaderPluginRegistryServiceImpl();
		context.registerService(ReaderPluginRegistryService.class.getName(),
				readerPluginRegistryService, null);

		System.out.println("Registering Service:"
				+ " ReaderConnectionRegisrtyService");
		connectionService = new ReaderConnectionRegistryServiceImpl();
		readerPluginRegistryService
				.addEventListener((ReaderConnectionRegistryServiceImpl) connectionService);
		context.registerService(
				ReaderConnectionRegistryService.class.getName(),
				connectionService, null);

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
