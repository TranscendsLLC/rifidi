package org.rifidi.edge.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryServiceImpl;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.core.session.SessionRegistryServiceImpl;

public class Activator implements BundleActivator {

	private SessionRegistryService sessionService;
	private ReaderAdapterRegistryService readerAdapterRegistryService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Bundle " + this.getClass().getName() + " loaded");

		System.out.println("Registering SessionRegistryService");
		sessionService = new SessionRegistryServiceImpl();
		context.registerService(SessionRegistryService.class.getName(),
				sessionService, null);

		System.out.println("Registering ReaderAdapterRegistryService");
		readerAdapterRegistryService = new ReaderAdapterRegistryServiceImpl();
		context.registerService(ReaderAdapterRegistryService.class.getName(),
				readerAdapterRegistryService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Bundle " + this.getClass().getName() + " stopped");
	}

}
