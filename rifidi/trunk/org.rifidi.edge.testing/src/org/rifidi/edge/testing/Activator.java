package org.rifidi.edge.testing;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.testing.service.TestingService;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class Activator implements BundleActivator {

	AbstractThread testingThread;
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle TestingService Started ==");
		testingThread = new TestingServiceImpl("TestingService Thread");
		testingThread.start();
		context.registerService(TestingService.class.getName(),
				testingThread, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		testingThread.stop();
		System.out.println("== Bundle TestingService Stopped ==");
	}

}
