package org.rifidi.edge.core.communication;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;

/**
 * @author jerry
 * 
 */
public class Activator implements BundleActivator {

	private CommunicationServiceImpl communicationService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");

		System.out.println("Registering Service: CommunicationService");

		communicationService = new CommunicationServiceImpl();
		context.registerService(CommunicationServiceImpl.class.getName(),
				communicationService, null);

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
