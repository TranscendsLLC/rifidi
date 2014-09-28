package org.rifidi.edge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	/** Logger */
	private final Log logger = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		try {
			boolean enabled = Boolean.parseBoolean(System
					.getProperty("org.rifidi.mqtt.enabled"));

			if (enabled) {
				logger.info("Starting Moquette MQTT");
				this.server = new Server();
				this.server.startServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
