package org.rifidi.edge.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryServiceImpl;
import org.rifidi.edge.core.readerPluginService.ReaderAdapterRegistryService;
import org.rifidi.edge.core.readerPluginService.ReaderAdapterRegistryServiceImpl;

public class Activator implements BundleActivator {

	private ReaderConnectionRegistryService sessionService;
	private ReaderAdapterRegistryService readerAdapterRegistryService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Bundle " + this.getClass().getName() + " loaded");

		System.out.println("Registering SessionRegistryService");
		sessionService = new ReaderConnectionRegistryServiceImpl();
		context.registerService(ReaderConnectionRegistryService.class.getName(),
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
