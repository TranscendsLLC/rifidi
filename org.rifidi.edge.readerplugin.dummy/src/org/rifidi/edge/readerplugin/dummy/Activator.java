package org.rifidi.edge.readerplugin.dummy;

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
		System.out.println("== Bundle DummyReaderPlugin started ==");
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		readerPluginService.unregisterReaderPlugin(DummyReaderInfo.class);
		System.out.println("== Bundle DummyReaderPlugin stopped ==");

	}

	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
		System.out.println("Registering Plugin: DummyReaderPlugin");
		readerPluginService.registerReaderPlugin(DummyReaderInfo.class,
				new DummyReaderPlugin());
	}

}
