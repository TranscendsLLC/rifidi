package org.rifidi.edge;

import org.dna.mqtt.moquette.server.Server;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * 
 */

/**
 * @author matt
 * 
 */
public class Activator implements BundleActivator {

	private Server server;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {		
		this.server = new Server();
		this.server.startServer();	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		this.server.stopServer();
	}

}
