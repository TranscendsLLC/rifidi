package org.rifidi.edge.readerplugin.alien;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class Activator implements BundleActivator {

	private ReaderPluginService readerPluginService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle AlienReaderPlugin started ==");
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (readerPluginService != null) {
			System.out.println("Unregistering Plugin: AlienReaderPlugin");
			readerPluginService.unregisterReaderPlugin(AlienReaderInfo.class);
		}
		System.out.println("== Bundle AlienReaderPlugin stopped ==");
	}

	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
		System.out.println("Registering Plugin: AlienReaderPlugin");
		this.readerPluginService.registerReaderPlugin(AlienReaderInfo.class,
				new AlienReaderPlugin());
	}

}
