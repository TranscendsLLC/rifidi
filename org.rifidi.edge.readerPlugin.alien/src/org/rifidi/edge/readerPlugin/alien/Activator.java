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
package org.rifidi.edge.readerPlugin.alien;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class Activator implements BundleActivator {

	private ReaderPluginRegistryService readerPluginRegistryService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		readerPluginRegistryService
				.unregisterReaderAdapter(AlienReaderInfo.class);
		System.out.println("Bundle " + this.getClass().getName() + " stopped");
	}

	/**
	 * Gets the registry service for this plugin.
	 * 
	 * @return The registry service for this plugin.
	 */
	public ReaderPluginRegistryService getReaderAdapterRegistryService() {
		return readerPluginRegistryService;
	}

	/**
	 * Sets the registry service for this plugin.
	 * 
	 * @param readerPluginRegistryService
	 *            The new registry service for this plugin.
	 */
	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;

		System.out.println("Registering ReaderPlugin: Alien");
		// register ReaderAdapter to the Services Registry
		readerPluginRegistryService.registerReaderAdapter(
				AlienReaderInfo.class, new AlienReaderPluginFactory());
	}

}
