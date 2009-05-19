package org.rifidi.edge.readerplugin.alien;

import java.util.Dictionary;

import org.rifidi.edge.core.notifications.NotifierService;

/**
 * A wrapper around the JMS Notifier Service so that it can be passed in to
 * created objects even if the notifier is not available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class NotifierServiceWrapper {

	/** The JMS notifier */
	private NotifierService notifierService;

	/**
	 * Returns the NotifierService.
	 * 
	 * @return
	 */
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
