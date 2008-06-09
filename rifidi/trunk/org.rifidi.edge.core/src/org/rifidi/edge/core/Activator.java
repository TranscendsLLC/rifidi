package org.rifidi.edge.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryServiceImpl;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.core.session.SessionRegistryServiceImpl;

public class Activator implements BundleActivator {

	SessionRegistryService sessionService;
	ReaderAdapterRegistryService readerAdapterRegistryService;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		sessionService = new SessionRegistryServiceImpl();
		context.registerService(SessionRegistryService.class.getName(), sessionService, null);
		
		readerAdapterRegistryService = new ReaderAdapterRegistryServiceImpl();
		context.registerService(ReaderAdapterRegistryService.class.getName(), readerAdapterRegistryService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
