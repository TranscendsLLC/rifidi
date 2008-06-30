package org.rifidi.edge.jms.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.connection.jms.JMSService;
import org.rifidi.edge.jms.service.impl.JMSServiceImpl;

public class Activator implements BundleActivator {

	private JMSServiceImpl jmsService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");

		System.out.println("Registering Service: JMSService");
		jmsService = new JMSServiceImpl();
		context.registerService(JMSService.class.getName(), jmsService, null);
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
