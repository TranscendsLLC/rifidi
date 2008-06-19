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
package org.rifidi.edge.readerPlugin.llrp;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class is the activator which starts and stops the bundle.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
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
				.unregisterReaderAdapter(LLRPReaderInfo.class);
		System.out.println("== Bundle " + this.getClass().getName()
				+ " stopped ==");
	}

	/**
	 * Gets the plugin registry service.  
	 * 
	 * @return	The plugin registry service.  
	 */
	public ReaderPluginRegistryService getReaderAdapterRegistryService() {
		return readerPluginRegistryService;
	}

	/**
	 * Sets the plugin registry service.  
	 * 
	 * @param readerPluginRegistryService	The new plugin registry service.
	 */
	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;

		System.out.println("Registering ReaderPlugin: LLRPReader");
		// register ReaderAdapter to the Services Registry
		readerPluginRegistryService.registerReaderAdapter(LLRPReaderInfo.class,
				new LLRPReaderPluginFactory());
	}

}
