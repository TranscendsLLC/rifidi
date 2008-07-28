package org.rifidi.edge.readerplugin.thingmagic;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class Activator implements BundleActivator {

	private ReaderPluginService readerPluginService;
	private BundleContext context;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle ThingMagicReaderPlugin started ==");
		ServiceRegistry.getInstance().service(this);
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (readerPluginService != null) {
			System.out.println("Unregistering Plugin: ThingMagicReaderPlugin");
			readerPluginService.unregisterReaderPlugin(context.getBundle());
		}
		System.out.println("== Bundle ThingMagicReaderPlugin stopped ==");
	}

	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
		System.out.println("Registering Plugin: ThingMagicReaderPlugin");
		this.readerPluginService.registerReaderPlugin(context.getBundle());
		context = null;	
	}
}
