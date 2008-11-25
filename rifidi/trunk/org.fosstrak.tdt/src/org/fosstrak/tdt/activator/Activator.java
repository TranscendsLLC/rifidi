package org.fosstrak.tdt.activator;

import org.fosstrak.tdt.fileservice.FileService;
import org.fosstrak.tdt.pool.TDTEnginePool;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		FileService fileService = new FileService(context.getBundle());
		context.registerService(FileService.class.getName(), fileService, null);
		
		TDTEnginePool pool = new TDTEnginePool();
		context.registerService(TDTEnginePool.class.getName(), pool, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
