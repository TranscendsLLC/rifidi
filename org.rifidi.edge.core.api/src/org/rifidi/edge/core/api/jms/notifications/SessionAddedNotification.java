package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class SessionAddedNotification implements Serializable{
	
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
