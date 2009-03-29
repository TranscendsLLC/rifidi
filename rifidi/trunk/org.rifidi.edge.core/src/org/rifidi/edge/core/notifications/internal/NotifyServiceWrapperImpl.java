/**
 * 
 */
package org.rifidi.edge.core.notifications.internal;

import java.util.Dictionary;

import org.rifidi.edge.core.notifications.NotifierService;
import org.rifidi.edge.core.notifications.NotifierServiceWrapper;

/**
 * @author kyle
 *
 */
public class NotifyServiceWrapperImpl implements NotifierServiceWrapper{
	
	private NotifierService service = null;

	/**
	 * @return the service
	 */
	@Override
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
