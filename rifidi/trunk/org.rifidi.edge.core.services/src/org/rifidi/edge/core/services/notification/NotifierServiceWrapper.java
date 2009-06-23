package org.rifidi.edge.core.services.notification;

/**
 * This object is instantiated by spring and wraps the NotifierService. This
 * object can be passed directly into beans. They can then obtain a reference to
 * the Notifier Service if it is available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface NotifierServiceWrapper {

	/**
	 * 
	 * @return The Notifier Service
	 */
	public NotifierService getService();

}
