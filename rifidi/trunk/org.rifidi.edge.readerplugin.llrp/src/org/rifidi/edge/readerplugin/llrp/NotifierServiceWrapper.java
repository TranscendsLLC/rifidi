package org.rifidi.edge.readerplugin.llrp;

import java.util.Dictionary;

import org.rifidi.edge.core.notifications.NotifierService;

public class NotifierServiceWrapper {
	/** The JMS notifier */
	private NotifierService notifierService;

	public NotifierService getNotifierService() {
		return notifierService;
	}

	/**
	 * Called by spring
	 * 
	 * @param service
	 */
	public void bindNotifierService(NotifierService service,
			Dictionary<String, String> parameters) {
		this.notifierService = service;
	}

	/**
	 * Called by spring
	 * 
	 * @param service
	 */
	public void unbindNotifierService(NotifierService service,
			Dictionary<String, String> parameters) {
		this.notifierService = null;
	}

}
