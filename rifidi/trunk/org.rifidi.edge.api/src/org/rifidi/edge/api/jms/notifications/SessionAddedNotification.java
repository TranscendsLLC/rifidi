package org.rifidi.edge.api.jms.notifications;

import java.io.Serializable;

/**
 * A notification sent to a client when a session has been created
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SessionAddedNotification implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;
	/** The ID of the session */
	private String sessionID;

	/**
	 * Constructor.
	 * 
	 * @param readerID
	 * @param sessionID
	 */
	public SessionAddedNotification(String readerID, String sessionID) {
		this.readerID = readerID;
		this.sessionID = sessionID;
	}

	/**
	 * Returns the reader ID.
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the session ID.
	 * 
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

}
