package com.csc.rfid.toolcrib;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Activator for the bundle. The start() method is called when the bundle is
 * starting up. The stop method is called when it is shutting down
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Activator implements BundleActivator {

	/** A reference to the instance of MyApplication */
	protected volatile static ToolcribApp myApp;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// stop MyApplication when the bundle shuts down
		myApp.stop();
	}

}
