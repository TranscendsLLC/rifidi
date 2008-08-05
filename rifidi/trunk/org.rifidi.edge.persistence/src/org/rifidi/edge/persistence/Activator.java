/* 
 * Activator.java
 *  Created:	July 17th, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.persistence.service.PersistenceService;
import org.rifidi.edge.persistence.service.impl.PersistanceServiceImpl;

/**
 * The activator for the persistence class. This needs to start the persistence
 * service.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");

		//Create and register the persistence service.  
		PersistenceService persistSer = new PersistanceServiceImpl();
		context.registerService(persistSer.getClass().getName(), persistSer,
				null);
		//persistance starter thread comes on when ReaderPluginService is registered
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
