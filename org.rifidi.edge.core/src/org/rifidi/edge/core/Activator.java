package org.rifidi.edge.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryServiceImpl;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryServiceImpl;

public class Activator implements BundleActivator {

	private ReaderConnectionRegistryService sessionService;
	private ReaderPluginRegistryService readerPluginRegistryService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName() + " loaded ==");

		
		System.out.println("Registering Service: ReaderPluginRegistryService");
		readerPluginRegistryService = new ReaderPluginRegistryServiceImpl();
		context.registerService(ReaderPluginRegistryService.class.getName(),
				readerPluginRegistryService, null);
		
		System.out.println("Registering Service: ReaderConnectionRegisrtyService");
		sessionService = new ReaderConnectionRegistryServiceImpl();
		context.registerService(ReaderConnectionRegistryService.class.getName(),
				sessionService, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName() + " stopped ==");
	}

}
