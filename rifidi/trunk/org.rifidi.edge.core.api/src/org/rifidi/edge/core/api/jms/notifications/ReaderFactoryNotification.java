/**
 * 
 */
package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * A JMS Notification Event for when ReaderFactories are added or removed.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderFactoryNotification implements Serializable {

	/** The SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String id;
	/** whether the reader was added or removed */
	private ReaderFactoryEventType eventType;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The ID of the factory
	 * @param eventType
	 *            The Type of ReaderFactoryEvent
	 */
	public ReaderFactoryNotification(String id, ReaderFactoryEventType eventType) {
		super();
		this.id = id;
		this.eventType = eventType;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the eventType
	 */
	public ReaderFactoryEventType getEventType() {
		return eventType;
	}

	/**
	 * An enum that describes the type of this event
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	public enum ReaderFactoryEventType {
		ADDED, REMOVED;
	}

}
