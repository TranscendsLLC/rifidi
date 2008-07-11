package org.rifidi.edge.persistence;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sandbox.LoadTestClass;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		// SandboxClass omg = new SandboxClass();
		// omg.test();
		LoadTestClass ltc = new LoadTestClass();
		ltc.test();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
