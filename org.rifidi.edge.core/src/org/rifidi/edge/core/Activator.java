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
import org.rifidi.edge.core.connection.jms.JMSService;
import org.rifidi.edge.core.connection.jms.JMSServiceImpl;
import org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService;
import org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryServiceImpl;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryServiceImpl;


public class Activator implements BundleActivator {

	private ReaderConnectionRegistryService sessionService;
	private ReaderPluginRegistryService readerPluginRegistryService;
	private JMSServiceImpl jmsService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName() + " loaded ==");

		
		System.out.println("Registering Service: ReaderPluginRegistryService");
		readerPluginRegistryService = new ReaderPluginRegistryServiceImpl();
		context.registerService(ReaderPluginRegistryService.class.getName(),
				readerPluginRegistryService, null);
		
		System.out.println("Registering Service: ReaderConnectionRegisrtyService");
		sessionService = new ReaderConnectionRegistryServiceImpl();
		context.registerService(ReaderConnectionRegistryService.class.getName(),
				sessionService, null);
		
		System.out.println("Registering Service: JMSService");
		jmsService = new JMSServiceImpl();
		context.registerService(JMSService.class.getName(),
				jmsService, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName() + " stopped ==");
	}

}
