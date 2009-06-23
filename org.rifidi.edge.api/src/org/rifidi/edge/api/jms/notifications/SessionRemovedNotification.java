package org.rifidi.edge.api.jms.notifications;

import java.io.Serializable;

/**
 * A notification send to a client when a session has been deleted
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SessionRemovedNotification implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;
	/** The ID of the session */
	private String sessionID;

	/**
	 * @param readerID
	 */
	public SessionRemovedNotification(String readerID, String sessionID) {
		this.readerID = readerID;
		this.sessionID = sessionID;
	}

	/**
	 * Returns the ID of the reader.
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the ID of the session.
	 * 
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

}
