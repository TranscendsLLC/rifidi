package org.rifidi.edge.core.messagequeue;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.messagequeue.service.MessageServiceImpl;

/**
 * Activator of the MessageQueue Bundle
 * 
 * @author Andreas Huebner - andreas@pramari.com
 *
 */
public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle MessageService started ==");
		System.out.println("Registering Service: MessageService");

		MessageService messageService = new MessageServiceImpl();
		context.registerService(MessageService.class.getName(), messageService,
				null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("== Bundle MessageService stopped ==");
	}
}
