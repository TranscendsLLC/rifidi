package org.rifidi.edge.core.communication;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.communication.service.ConnectionServiceImpl;

public class Activator implements BundleActivator {

	private ConnectionServiceImpl connectionService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle ConnectionService started ==");
		System.out.println("Registering Service: ConnectionService");

		connectionService = new ConnectionServiceImpl();
		context.registerService(ConnectionService.class.getName(),
				connectionService, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle ConnectionService stopped ==");
	}

}
