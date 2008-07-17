package org.rifidi.edge.core.rmi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.rmi.service.RMIServerService;
import org.rifidi.edge.core.rmi.service.impl.RMIServerServiceImpl;

/**
 * Activator for the RMI Bundle
 * 
 * @author Andreas Huebner - andreas@pramari.com
 *
 */
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
