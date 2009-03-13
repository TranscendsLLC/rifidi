package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * A Notification that is sent when a reader is either Added or Removed from the
 * edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderNotification implements Serializable {

	/** Serial Version ID for this message */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String id;
	/** whether the reader was added or removed */
	private ReaderEventType eventType;

	/**
	 * @param id
	 *            The ID of the reader
	 * @param eventType
	 *            The type of event (either added or removed)
	 */
	public ReaderNotification(String id, ReaderEventType eventType) {
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
	public ReaderEventType getEventType() {
		return eventType;
	}

	/**
	 * The possible types for this event
	 */
	public enum ReaderEventType {
		ADDED, REMOVED
	}

}
