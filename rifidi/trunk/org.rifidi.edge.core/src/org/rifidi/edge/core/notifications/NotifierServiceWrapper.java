/**
 * 
 */
package org.rifidi.edge.core.notifications;

import java.util.Dictionary;

import org.rifidi.edge.notifications.NotifierService;

/**
 * This object is instantiated by spring and wraps the NotifierService. This
 * object can be passed directly into beans. They can then obtain a reference to
 * the Notifier Service if it is available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class NotifierServiceWrapper {

	private NotifierService service = null;

	/**
	 * @return the service
	 */
	public NotifierService getService() {
		return service;
	}

	/**
	 * Called by spring when the Notifier Service is available
	 * 
	 * @param service
	 * @param properties
	 */
	public void bindNotifierService(NotifierService service,
			Dictionary<String, String> properties) {
		this.service = service;
	}

	/**
	 * Called by spring when the Notifier Service becomes unavailable
	 * 
	 * @param service
	 * @param properties
	 */
	public void unbindNotifierService(NotifierService service,
			Dictionary<String, String> properties) {
		this.service = null;
	}

}
