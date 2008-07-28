/* 
 * Activator.java
 *  Created:	Jun 20, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * Activator for Alien reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class Activator implements BundleActivator {

	private ReaderPluginService readerPluginService;
	/**
	 * This is just temporary
	 */
	private BundleContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle AlienReaderPlugin started ==");
		this.context = context;
		ServiceRegistry.getInstance().service(this);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (readerPluginService != null) {
			System.out.println("Unregistering Plugin: AlienReaderPlugin");
			readerPluginService.unregisterReaderPlugin(context.getBundle());
		}
		System.out.println("== Bundle AlienReaderPlugin stopped ==");
	}

	/**
	 * Set the ReaderPluginService via injection.
	 * 
	 * @param readerPluginService
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
		System.out.println("Registering Plugin: AlienReaderPlugin");
		this.readerPluginService.registerReaderPlugin(context.getBundle());
		context = null;
	}
}
