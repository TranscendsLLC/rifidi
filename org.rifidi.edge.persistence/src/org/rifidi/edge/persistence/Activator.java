package org.rifidi.edge.persistence;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.persistence.service.EdgePersistenceService;
import org.rifidi.edge.persistence.service.EdgePersistenceServiceImpl;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");

		System.out.println("Registering Service: EdgePersistenceService");
		EdgePersistenceServiceImpl edgePersistenceService = new EdgePersistenceServiceImpl();
		context.registerService(EdgePersistenceService.class.getName(),
				edgePersistenceService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " stopped ==");
	}

}
