package org.rifidi.edge.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readerplugin.service.impl.ReaderPluginServiceImpl;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.core.readersession.service.impl.ReaderSessionServiceImpl;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle Core started ==");
		System.out.println("Registering Service: ReaderPluginService");

		ReaderPluginService readerPluginService = new ReaderPluginServiceImpl();
		context.registerService(ReaderPluginService.class.getName(),
				readerPluginService, null);

		System.out.println("Registering Service: ReaderSessionService");
		ReaderSessionService readerSessionService = new ReaderSessionServiceImpl(
				readerPluginService);
		context.registerService(ReaderSessionService.class.getName(), readerSessionService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle ConnectionService stopped ==");
	}

}
