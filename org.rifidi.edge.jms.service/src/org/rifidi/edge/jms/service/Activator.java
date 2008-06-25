package org.rifidi.edge.jms.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.jms.service.impl.JMSService;
import org.rifidi.edge.jms.service.impl.JMSServiceImpl;

/**
 * @author jerry
 *
 */
public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName() + " loaded ==");
		
		System.out.println("Registering Service: JMSService");
		JMSService service = new JMSServiceImpl();
		context.registerService(JMSService.class.getName(), service, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName() + " stopped ==");
	}

}
