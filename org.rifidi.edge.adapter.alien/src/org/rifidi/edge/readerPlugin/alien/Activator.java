package org.rifidi.edge.readerPlugin.alien;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerPluginService.ReaderAdapterRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class Activator implements BundleActivator {

	private ReaderAdapterRegistryService readerAdapterRegistryService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Bundle " + this.getClass().getName() + " loaded");
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		readerAdapterRegistryService.unregisterReaderAdapter(AlienReaderInfo.class);
		System.out.println("Bundle " + this.getClass().getName() + " stopped");
	}

	/**
	 * @return
	 */
	public ReaderAdapterRegistryService getReaderAdapterRegistryService() {
		return readerAdapterRegistryService;	
	}

    /**
     * @param readerAdapterRegistryService
     */
    @Inject
	public void setReaderAdapterRegistryService(
			ReaderAdapterRegistryService readerAdapterRegistryService) {
		this.readerAdapterRegistryService = readerAdapterRegistryService;
		
		System.out.println("Registering ReaderAdapter Alien.");
		// register ReaderAdapter to the Services Registry
		readerAdapterRegistryService.registerReaderAdapter(AlienReaderInfo.class,
				new AlienReaderPluginFactory());
	}
	
	

}
