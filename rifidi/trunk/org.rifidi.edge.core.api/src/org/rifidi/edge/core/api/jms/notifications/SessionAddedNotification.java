package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

public class SessionAddedNotification implements Serializable{
	
	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;
	/** The ID of the session */
	private String sessionID;

	/**
	 * @param readerID
	 */
	public SessionAddedNotification(String readerID, String sessionID) {
		this.readerID = readerID;
		this.sessionID = sessionID;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

}
